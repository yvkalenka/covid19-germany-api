package com.inws.cvd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inws.cvd.cache.CacheHelper;
import com.inws.cvd.client.dto.CasesDataResponse;
import com.inws.cvd.client.dto.RecoveredDataResponse;
import com.inws.cvd.initializer.RedisInitializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static com.inws.cvd.util.ResourceUtils.getResourceContents;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {RedisInitializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private CacheHelper cacheHelper;
    @MockBean
    private RestTemplate restTemplate;

    @SneakyThrows
    @BeforeEach
    public void init() {

        // given
        CasesDataResponse cases = objectMapper
                .readValue(getResourceContents("api/cases-history.json"), new TypeReference<>() {
                });
        RecoveredDataResponse recovered = objectMapper
                .readValue(getResourceContents("api/recovered-history.json"), new TypeReference<>() {
                });

        when(restTemplate.getForObject(anyString(), eq(CasesDataResponse.class)))
                .thenReturn(cases);
        when(restTemplate.getForObject(anyString(), eq(RecoveredDataResponse.class)))
                .thenReturn(recovered);
    }

    @AfterEach
    public void tearDown() {
        cacheHelper.clean();
    }

}
