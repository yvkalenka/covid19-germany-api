package com.inws.cvd.client;

import com.inws.cvd.client.dto.CasesDataResponse;
import com.inws.cvd.client.dto.RecoveredDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryClient {

    private final RestTemplate restTemplate;

    @Value(value = "${remote.corona-zahlen.url}")
    private String coronaZahlenUrl;

    @SneakyThrows
    public List<CasesDataResponse.CaseData> getCasesHistory() {
        var url = format("%s/cases", coronaZahlenUrl);

        var cases = restTemplate.getForObject(url, CasesDataResponse.class);

        return cases != null
                ? cases.getData()
                : Collections.emptyList();
    }

    @SneakyThrows
    public List<RecoveredDataResponse.RecoveredData> getRecoveredHistory() {
        var url = format("%s/recovered", coronaZahlenUrl);

        var recovered = restTemplate.getForObject(url, RecoveredDataResponse.class);

        return recovered != null
                ? recovered.getData()
                : Collections.emptyList();
    }

}
