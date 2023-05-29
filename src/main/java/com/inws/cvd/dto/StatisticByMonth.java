package com.inws.cvd.dto;

import lombok.Builder;

@Builder
public record StatisticByMonth(

        int year,
        int month,
        int totalCases,
        int totalRecovered,
        int minCases,
        int maxCases) {
}
