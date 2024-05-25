package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.service.IgrService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IgrController.class)
class IgrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IgrService igrService;

    @Test
    @DisplayName("클래스 A 누전 조회")
    void getClassIgr() throws Exception {
        // given
        given(igrService.getClassIgr()).willReturn(new Igr(1.0));

        // when
        // then
        mockMvc.perform(get("/api/sensor/igr/class")
                        .header("X-USER-ID", "test")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(1.0));
    }

    @Test
    @DisplayName("Office 누전 조회")
    void getOfficeIgr() throws Exception {
        // given
        given(igrService.getOfficeIgr()).willReturn(new Igr(1.0));

        // when
        // then
        mockMvc.perform(get("/api/sensor/igr/office")
                        .header("X-USER-ID", "test")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(1.0));
    }
}