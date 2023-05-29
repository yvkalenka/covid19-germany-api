package com.inws.cvd.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class RecoveredDataResponse {

    private List<RecoveredData> data;

    @Data
    public static class RecoveredData {

        private int recovered;
        private LocalDate date;

    }

}
