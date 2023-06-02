package com.inws.cvd.converter;

import com.inws.cvd.client.dto.CasesDataResponse.CaseData;
import com.inws.cvd.client.dto.RecoveredDataResponse.RecoveredData;
import com.inws.cvd.dto.StatisticByDate;
import com.inws.cvd.dto.StatisticByMonth;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

import static java.time.LocalDate.parse;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Component
public class StatisticsConverter {

    public StatisticByDate applyForDateSpecific(CaseData caseData, RecoveredData recoveredData) {
        return StatisticByDate.builder()
                .date(caseData.getDate().toString())
                .cases(caseData.getCases())
                .recovered(recoveredData.getRecovered())
                .build();
    }

    public List<StatisticByMonth> applyForMonthSpecific(List<StatisticByDate> statisticByDates) {
        var mapByYearMonth = statisticByDates
                .stream()
                .collect(groupingBy(entry -> YearMonth.from(parse(entry.date())), HashMap::new, toList()));

        return mapByYearMonth.entrySet()
                .stream()
                .map(entry -> StatisticByMonth
                        .builder()
                        .month(entry.getKey().getMonthValue())
                        .year(entry.getKey().getYear())
                        .minCases(findMinCases(entry.getValue()))
                        .maxCases(findMaxCases(entry.getValue()))
                        .totalCases(findTotalCases(entry.getValue()))
                        .totalRecovered(findTotalRecovered(entry.getValue()))
                        .build())
                .toList();
    }

    private int findMinCases(List<StatisticByDate> list) {
        return list.stream()
                .min(comparing(StatisticByDate::cases))
                .map(StatisticByDate::cases)
                .orElse(0);
    }

    private int findMaxCases(List<StatisticByDate> list) {
        return list.stream()
                .max(comparing(StatisticByDate::cases))
                .map(StatisticByDate::cases)
                .orElse(0);
    }

    private int findTotalCases(List<StatisticByDate> list) {
        return list.stream()
                .mapToInt(StatisticByDate::cases)
                .sum();
    }

    private int findTotalRecovered(List<StatisticByDate> list) {
        return list.stream()
                .mapToInt(StatisticByDate::recovered)
                .sum();
    }
}
