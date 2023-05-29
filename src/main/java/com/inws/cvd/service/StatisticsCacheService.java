package com.inws.cvd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inws.cvd.client.HistoryClient;
import com.inws.cvd.client.dto.CasesDataResponse.CaseData;
import com.inws.cvd.client.dto.RecoveredDataResponse.RecoveredData;
import com.inws.cvd.converter.StatisticsConverter;
import com.inws.cvd.dto.StatisticByDate;
import com.inws.cvd.dto.StatisticByMonth;
import com.inws.cvd.exception.NoDataAvailable;
import com.inws.cvd.validator.DateValidator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@AllArgsConstructor
@Service
public class StatisticsCacheService {

    private static final String STATISTICS_KEY_PREFIX = "statistics.dates";

    private final HistoryClient historyClient;
    private final RedisTemplate<String, StatisticByDate> statisticsRedisTemplate;
    private final DateValidator dateValidator;
    private final StatisticsConverter statisticsConverter;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<StatisticByDate> findByRange(LocalDate fromDate, LocalDate toDate) {
        dateValidator.validateDates(fromDate, toDate);

        var foundRecords = getFromCacheByRange(fromDate.toEpochDay(), toDate.toEpochDay());
        // A marker identifies lack of data in cache. Additional data will be requested to seed.
        if (foundRecords.size() != DAYS.between(fromDate, toDate) + 1) {
            persistDateSpecificData();
            // Make additional request to cache after data was persisted.
            foundRecords = getFromCacheByRange(fromDate.toEpochDay(), toDate.toEpochDay());
        }
        // Data can not be empty after seeding. This may indicate of some internal unhandled issues only.
        if (foundRecords.isEmpty()) {
            throw new NoDataAvailable("Data is unavailable due internal reasons. Please contact support team.");
        }
        return objectMapper.convertValue(foundRecords, new TypeReference<>() {
        });
    }

    public List<StatisticByMonth> findByLastMonths(int num) {
        // Initially remote api does not provide the info about recent 2 days.
        var toDate = LocalDate.now().minusDays(2);
        var fromDate = toDate.minusMonths(num);
        var foundRecords = findByRange(fromDate, toDate);
        return statisticsConverter.applyForMonthSpecific(foundRecords);
    }

    private void persistDateSpecificData() {
        log.info("Updating date specific records in cache. Starting...");
        try {
            var casesToPersist = collectToMap(historyClient.getCasesHistory(), CaseData::getDate);
            var recoveredToPersist = collectToMap(historyClient.getRecoveredHistory(), RecoveredData::getDate);

            if (casesToPersist.size() != recoveredToPersist.size()) {
                log.warn("Inconsistency in retrieved records detected. The amount of cases days records '{}' do not match the amount of " +
                        "recovered days records '{}'.", casesToPersist.size(), recoveredToPersist.size());
            }
            mergeAndPersist(casesToPersist, recoveredToPersist);
            var cacheRecordsPresent = getFromCacheByRange(MIN_VALUE, MAX_VALUE).size();
            log.info("Updating date specific records finished. Cache contains {} records.", cacheRecordsPresent);
        } catch (RestClientException e) {
            log.error("Exception occurred while updating date specific records in cache.", e);
        }
    }

    private Set<StatisticByDate> getFromCacheByRange(long val1, long val2) {
        var result = statisticsRedisTemplate.opsForZSet().rangeByScore(STATISTICS_KEY_PREFIX, val1, val2);
        return nonNull(result) ? result : Collections.emptySet();
    }

    private <T> Map<LocalDate, T> collectToMap(List<T> dataList, Function<T, LocalDate> extractor) {
        return dataList.stream()
                .filter(entry -> nonNull(extractor.apply(entry)))
                .collect(HashMap::new, (map, data) -> map.put(extractor.apply(data), data), HashMap::putAll);
    }

    private void mergeAndPersist(Map<LocalDate, CaseData> casesDataMap, Map<LocalDate, RecoveredData> recoveredDataMap) {
        casesDataMap.forEach((date, casesData) -> {
            var recoveredData = recoveredDataMap.get(date);
            if (isNotEmpty(recoveredData)) {
                // Adding entries with present both cases and recoveries data only.
                var statisticsByDate = statisticsConverter.applyForDateSpecific(casesData, recoveredData);
                statisticsRedisTemplate.opsForZSet()
                        .addIfAbsent(STATISTICS_KEY_PREFIX, statisticsByDate, date.toEpochDay());
            }
        });
    }
}
