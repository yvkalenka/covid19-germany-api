package com.inws.cvd.controller;

import com.inws.cvd.BaseIntegrationTest;
import com.inws.cvd.dto.StatisticByDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

import static com.inws.cvd.util.ResourceUtils.getResourceContents;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest extends BaseIntegrationTest {

    @Autowired
    RedisTemplate<String, StatisticByDate> redisTemplate;

    @DisplayName("Should persist cache and find valid records by date range.")
    @Test
    void testFindValidByDateRangeApi() throws Exception {

        // given
        var expected = getResourceContents("get-by-date-expected.json");

        // when
        var actual = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/statistics?fromDate=19.05.2023&toDate=24.05.2023")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(APPLICATION_JSON_VALUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var expectedNode = objectMapper.readTree(expected);
        var actualNode = objectMapper.readTree(actual);

        // then
        assertEquals("Response content compare.", expectedNode, actualNode);
    }

    @DisplayName("Should not persist cache and throw exception when dateFrom in future.")
    @Test
    void testThrowExceptionOnDateFromInFutureByDateRangeApi() throws Exception {

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/statistics?fromDate=19.05.2024&toDate=24.05.2023")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(APPLICATION_JSON_VALUE))
                .andDo(print());

        // then
        assertTrue("Cache keys are not present.", ObjectUtils.isEmpty(redisTemplate.keys("*")));
    }

    @DisplayName("Should not persist cache and throw exception when dateFrom is less than 48 hours.")
    @Test
    void testThrowExceptionOnDateFromLessThanThresholdByDateRangeApi() throws Exception {

        var url = String.format("/api/v1/statistics?fromDate=%s&toDate=24.05.2023", LocalDate.now());

        // when
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(APPLICATION_JSON_VALUE))
                .andDo(print());

        // then
        assertTrue("Cache keys are not present.", ObjectUtils.isEmpty(redisTemplate.keys("*")));
    }

    @DisplayName("Should persist cache and find valid records by months.")
    @Test
    void testFindValidByMonthsApi() throws Exception {

        // given
        var expected = getResourceContents("get-by-months-expected.json");

        // when
        var actual = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/statistics/months/1")
                        .contentType(APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON_VALUE))
                .andExpect(status()
                        .isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(APPLICATION_JSON_VALUE))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var expectedNode = objectMapper.readTree(expected);
        var actualNode = objectMapper.readTree(actual);

        // then
        assertEquals("Response content compare.", expectedNode, actualNode);

    }
}