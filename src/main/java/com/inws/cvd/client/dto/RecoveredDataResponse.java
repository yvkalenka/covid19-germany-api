package com.inws.cvd.client.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class RecoveredDataResponse {

    private List<RecoveredData> data;

    @Data
    public static class RecoveredData {

        private int recovered;
        private LocalDate date;

    }

}
