package com.inws.cvd.controller;

import com.inws.cvd.dto.StatisticByDate;
import com.inws.cvd.dto.StatisticByMonth;
import com.inws.cvd.service.StatisticsCacheService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/statistics", produces = APPLICATION_JSON_VALUE)
public class StatisticsController {

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";

    private final StatisticsCacheService statisticsCacheService;

    @GetMapping
    public List<StatisticByDate> findByRange(
            @RequestParam(value = "fromDate") @DateTimeFormat(pattern = DATE_FORMAT_PATTERN) LocalDate fromDate,
            @RequestParam(value = "toDate") @DateTimeFormat(pattern = DATE_FORMAT_PATTERN) LocalDate toDate) {
        return statisticsCacheService.findByRange(fromDate, toDate);
    }

    @GetMapping("/months/{num}")
    public List<StatisticByMonth> findByLastMonths(
            @PathVariable(value = "num") int num) {
        return statisticsCacheService.findByLastMonths(num);
    }
}
