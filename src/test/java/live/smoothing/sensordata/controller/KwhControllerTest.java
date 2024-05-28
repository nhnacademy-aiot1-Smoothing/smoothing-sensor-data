package live.smoothing.sensordata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.smoothing.sensordata.config.DateFormatConfiguration;
import live.smoothing.sensordata.dto.SensorPowerMetricResponse;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValueResponse;
import live.smoothing.sensordata.service.KwhService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(DateFormatConfiguration.class)
@WebMvcTest(KwhController.class)
class KwhControllerTest {

    @MockBean
    private KwhService kwhService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("X-USER-ID 헤더가 없을 때 401을 반환한다.")
    void getKwh_no_user_id() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage")
                .param("unit", "hour")
                .param("per", "1")
                .param("tags", "test")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("최근 48시간 전력량 데이터를 가져올 때 파라미터에 따라 적절한 횟수 및 응답이 반환된다.")
    void getKwh_1hour() throws Exception {
        TagPowerMetricResponse response = new TagPowerMetricResponse(
                new ArrayList<>(),
                new ArrayList<>()
        );

        // given
        given(kwhService.get48HourData(anyString())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage")
                .param("unit", "hour")
                .param("per", "1")
                .param("tags", "test")
                .header("X-USER-ID", "test-user")
        )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).get48HourData(anyString());
    }

    @Test
    @DisplayName("최근 2주 전력량 데이터를 가져올 때 파라미터에 따라 적절한 횟수 및 응답이 반환된다.")
    void getKwh_day() throws Exception {
        TagPowerMetricResponse response = new TagPowerMetricResponse(
                new ArrayList<>(),
                new ArrayList<>()
        );

        // given
        given(kwhService.get2WeekData(anyString())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage")
                        .param("unit", "day")
                        .param("per", "1")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).get2WeekData(anyString());
    }

    @Test
    @DisplayName("전력량 데이터를 가져올 때 파라미터에 매핑되는 메서드가 없으면 404를 반환한다.")
    void getKwh_not_found_servlet() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage")
                        .param("unit", "day")
                        .param("per", "2")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Kwh 데이터를 가져올 때 파라미터가 비어있을 경우 status code 400을 반환한다.")
    void getKwh_day_missing_parameter() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage")
                        .param("per", "1")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isBadRequest());
    }


        @Test
    @DisplayName("최근 일주일 시간대별 전력량 요청을 보내면 적절한 응답이 반환된다.")
    void getWeeklyDataByTimeOfDay() throws Exception {
        // given
        TimeZoneResponse response = new TimeZoneResponse(new ArrayList<>());

        given(kwhService.getWeeklyDataByTimeOfDay()).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/weekly/timezone")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).getWeeklyDataByTimeOfDay();
    }

    @Test
    @DisplayName("특정 기간 일별 전체 전력량 조회 요청을 보내면 적절한 응답이 반환된다.")
    void getDailyTotalDataByPeriod() throws Exception {
        // given
        TagPowerMetricResponse response = new TagPowerMetricResponse(
                new ArrayList<>(),
                new ArrayList<>()
        );

        given(kwhService.getDailyTotalDataByPeriod(any(), any(), anyString())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/daily/period/total")
                        .param("start", "2021-01-01")
                        .param("end", "2021-01-02")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).getDailyTotalDataByPeriod(any(), any(), anyString());
    }

    @Test
    @DisplayName("특정 기간 일별 전체 전력량 조회 요청을 보낼 때 파라미터 타입이 맞지 않으면 status code 400을 반환한다.")
    void getDailyTotalDataByPeriod_parameter_type_mismatch() throws Exception {
        // given
        TagPowerMetricResponse response = new TagPowerMetricResponse(
                new ArrayList<>(),
                new ArrayList<>()
        );

        given(kwhService.getDailyTotalDataByPeriod(any(), any(), anyString())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/daily/period/total")
                        .param("start", "2021-01-01T00:00:00")
                        .param("end", "2021-01-02")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("특정 기간 일별 센서별 전력량 조회 요청을 보내면 적절한 응답이 반환된다.")
    void getDailyDataByPeriod() throws Exception {
        // given
        SensorPowerMetricResponse response = new SensorPowerMetricResponse(new ArrayList<>());

        given(kwhService.getDailySensorDataByPeriod(any(), any(), anyString())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/daily/period")
                        .param("start", "2021-01-01")
                        .param("end", "2021-01-02")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).getDailySensorDataByPeriod(any(), any(), anyString());
    }

    @Test
    @DisplayName("최근 24시간 센서별 전력량 조회 요청을 보내면 적절한 응답이 반환된다.")
    void getDailyTotalSensorData() throws Exception {
        // given
        TagSensorValueResponse response = new TagSensorValueResponse(new ArrayList<>());

        given(kwhService.getTotalSensorData(anyString(), any(), any())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/daily/value/total")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).getTotalSensorData(anyString(), any(), any());
    }

    @Test
    @DisplayName("최근 일주일 센서별 전력량 조회 요청을 보내면 적절한 응답이 반환된다.")
    void getWeeklyTotalSensorData() throws Exception {
        // given
        TagSensorValueResponse response = new TagSensorValueResponse(new ArrayList<>());

        given(kwhService.getTotalSensorData(anyString(), any(), any())).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/weekly/value/total")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).getTotalSensorData(anyString(), any(), any());
    }

    @Test
    @DisplayName("시간별 전체 전력량 조회 요청을 보내면 적절한 응답이 반환된다.")
    void getHourlyTotalData() throws Exception {
        // given
        TagPowerMetricResponse response = new TagPowerMetricResponse(
                new ArrayList<>(),
                new ArrayList<>()
        );

        given(kwhService.getHourlyTotalData()).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/kwh/usage/hourly/total")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk());

        verify(kwhService, times(1)).getHourlyTotalData();
    }
}