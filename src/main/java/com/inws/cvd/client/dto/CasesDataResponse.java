package com.inws.cvd.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class CasesDataResponse {

    private List<CaseData> data;

    @Data
    public static class CaseData {

        private int cases;
        private LocalDate date;

    }
}
