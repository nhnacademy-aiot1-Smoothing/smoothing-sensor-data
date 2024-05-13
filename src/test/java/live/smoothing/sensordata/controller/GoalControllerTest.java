package live.smoothing.sensordata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.service.GoalService;
import live.smoothing.sensordata.service.KwhService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GoalController.class)
class GoalControllerTest {

    @MockBean
    private GoalService goalService;

    @MockBean
    private KwhService kwhService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("목표 데이터를 가져올 때 적절한 응답이 반환된다.")
    void getGoalData() throws Exception {
        // given
        given(goalService.getGoal()).willReturn(new GoalResponse(0.0, 0));

        // when
        // then
        mockMvc.perform(get("/api/sensor/goals")
                    .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalAmount").value(0L))
                .andExpect(jsonPath("$.unitPrice").value(0));
    }

    @Test
    @DisplayName("목표 데이터를 저장할 때 해당 연월의 목표를 찾지 못하면 새로운 목표를 저장한다.")
    void saveGoal() throws Exception {
        // given
        given(goalService.existsByGoalDate()).willReturn(false);
        GoalRequest goalRequest = new GoalRequest(0.0, 0);

        // when
        // then
        mockMvc.perform(post("/api/sensor/goals")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(goalRequest))
                    .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isCreated());

        then(goalService).should(times(1)).saveGoal(any());
        then(goalService).should(times(0)).modifyGoal(any());
    }

    @Test
    @DisplayName("목표 데이터를 저장할 때 해당 연월의 목표를 찾으면 목표를 수정한다.")
    void modifyGoal() throws Exception {
        // given
        given(goalService.existsByGoalDate()).willReturn(true);
        GoalRequest goalRequest = new GoalRequest(0.0, 0);

        // when
        // then
        mockMvc.perform(post("/api/sensor/goals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(goalRequest))
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isCreated());

        then(goalService).should(times(0)).saveGoal(any());
        then(goalService).should(times(1)).modifyGoal(any());
    }

    @Test
    void getGoalHistory() throws Exception {

        // given
        List<GoalHistoryResponse> goalHistoryResponses = List.of(
                new GoalHistoryResponse(
                        LocalDateTime.of(2021, 3, 1, 0, 0),
                        3000.0,
                        300.0
                ),

                new GoalHistoryResponse(
                        LocalDateTime.of(2021, 2, 1, 0, 0),
                        2000.0,
                        200.0
                ),

                new GoalHistoryResponse(
                        LocalDateTime.of(2021, 1, 1, 0, 0),
                        1000.0,
                        100.0
                )
        );

        given(goalService.getGoalHistory(2021)).willReturn(goalHistoryResponses);
        given(kwhService.getCurrentMonthKwh()).willReturn(100.0);

        // when
        // then
        mockMvc.perform(get("/api/sensor/goals/history?year=2021")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2021-03-01T00:00:00"))
                .andExpect(jsonPath("$[0].goalAmount").value(3000L))
                .andExpect(jsonPath("$[0].amount").value(300))
                .andExpect(jsonPath("$[1].date").value("2021-02-01T00:00:00"))
                .andExpect(jsonPath("$[1].goalAmount").value(2000L))
                .andExpect(jsonPath("$[1].amount").value(200))
                .andExpect(jsonPath("$[2].date").value("2021-01-01T00:00:00"))
                .andExpect(jsonPath("$[2].goalAmount").value(1000L))
                .andExpect(jsonPath("$[2].amount").value(100));

        then(goalService).should(times(1)).getGoalHistory(2021);
    }

    @Test
    @DisplayName("현재 월의 목표 전력량을 가져올 때 적절한 응답이 반환된다.")
    void getCurrentMonthKwhGoal() throws Exception {
        // given
        given(goalService.getGoal()).willReturn(new GoalResponse(123.0, 0));
        given(kwhService.getCurrentMonthKwh()).willReturn(120.0);

        // when
        // then
        mockMvc.perform(get("/api/sensor/goals/kwh")
                        .header("X-USER-ID", "test-user")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalAmount").value(123.0))
                .andExpect(jsonPath("$.currentAmount").value(120.0));

        then(goalService).should(times(1)).getGoal();
        then(kwhService).should(times(1)).getCurrentMonthKwh();
    }
}