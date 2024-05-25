package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.service.GoalService;
import live.smoothing.sensordata.service.KwhService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CostController.class)
class CostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KwhService kwhService;

    @MockBean
    private GoalService goalService;

    @Test
    @DisplayName("실시간 전기 요금을 반환한다.")
    void getCost() throws Exception {
        // given
        given(kwhService.getCurrentMonthKwh()).willReturn(100.0);
        given(goalService.getGoal()).willReturn(new GoalResponse(1000.0, 100));

        // when
        // then
        mockMvc.perform(get("/api/sensor/cost")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cost").value(10000));

        verify(kwhService, times(1)).getCurrentMonthKwh();
        verify(goalService, times(1)).getGoal();
    }
}