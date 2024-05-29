package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.phase.Phase;
import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.dto.phase.ThreePhase;
import live.smoothing.sensordata.service.ThreePhaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThreePhaseController.class)
class ThreePhaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThreePhaseService threePhaseService;

    @Test
    @DisplayName("3상 전압 조회")
    void getThreePhaseData() throws Exception {
        // given
        PhaseResponse response = new PhaseResponse(
                List.of(
                        new ThreePhase(
                                "Class A",
                                new Phase(Instant.now(), 1.0),
                                new Phase(Instant.now(), 2.0)
                        ),
                        new ThreePhase(
                                "Office",
                                new Phase(Instant.now(), 3.0),
                                new Phase(Instant.now(), 4.0)
                        )
                )
        );
        given(threePhaseService.getThreePhase()).willReturn(response);

        // when
        // then
        mockMvc.perform(get("/api/sensor/three-phase")
                        .header("X-USER-ID", "test")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.threePhases[0].top.value").value(1.0))
                .andExpect(jsonPath("$.threePhases[0].bottom.value").value(2.0))
                .andExpect(jsonPath("$.threePhases[1].top.value").value(3.0))
                .andExpect(jsonPath("$.threePhases[1].bottom.value").value(4.0));
    }
}