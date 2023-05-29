package com.inws.cvd.client.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CasesDataResponse {

    private List<CaseData> data;

    @Data
    public static class CaseData {

        private int cases;
        private LocalDate date;

    }
}
