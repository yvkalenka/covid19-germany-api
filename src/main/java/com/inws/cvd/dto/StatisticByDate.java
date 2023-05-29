package com.inws.cvd.dto;

import lombok.Builder;

@Builder
public record StatisticByDate(

        int cases,
        int recovered,
        String date) {
}
