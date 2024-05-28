package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.service.WattService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WattController.class)
class WattControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WattService wattService;

    @Test
    @DisplayName("최근 10분간의 전력량 데이터를 가져올 때 파라미터에 따라 적절한 횟수 및 응답이 반환된다.")
    void getWattData_10m() throws Exception {
        // given
        TagPowerMetricResponse response = new TagPowerMetricResponse(new ArrayList<>(), new ArrayList<>());

        given(wattService.get10MinuteWattData("test")).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/watt/usage")
                .param("unit", "min")
                .param("per", "10")
                .param("tags", "test")
                .header("X-USER-ID", "test-user")
        )
                .andExpect(status().isOk());

        verify(wattService, times(1)).get10MinuteWattData("test");
    }

    @Test
    @DisplayName("최근 1시간의 전력량 데이터를 가져올 때 파라미터에 따라 적절한 횟수 및 응답이 반환된다.")
    void getWattData_hour() throws Exception {
        // given
        TagPowerMetricResponse response = new TagPowerMetricResponse(new ArrayList<>(), new ArrayList<>());

        given(wattService.get1HourWattData("test")).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/watt/usage")
                .param("unit", "hour")
                .param("per", "1")
                .param("tags", "test")
                .header("X-USER-ID", "test-user")
        )
                .andExpect(status().isOk());

        verify(wattService, times(1)).get1HourWattData("test");
    }

    @Test
    @DisplayName("필수 파라미터가 누락되었을 때 status code 400을 반환한다.")
    void getWattData_missing_parameter() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/sensor/watt/usage")
                        .param("per", "1")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전력 데이터를 가져올 때 파라미터에 매핑되는 메서드가 없으면 404를 반환한다.")
    void getWattData_not_found_servlet() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/api/sensor/watt/usage")
                        .param("unit", "hour")
                        .param("per", "2")
                        .param("tags", "test")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isNotFound());
    }
}