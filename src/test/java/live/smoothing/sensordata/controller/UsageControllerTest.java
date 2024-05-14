package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.usage.EnergyUsage;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import live.smoothing.sensordata.service.UsageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(UsageController.class)
@ExtendWith(MockitoExtension.class)
public class UsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsageService usageService;

    @Test
    public void testGetEnergyUsage() throws Exception {
        EnergyUsageResponse mockResponse = new EnergyUsageResponse(); // 예상 응답 객체 설정
        mockResponse.setWholeCountry(new EnergyUsage());
        mockResponse.setKimCity(new EnergyUsage());

        when(usageService.getEnergyUsage(2020, "March", "A001")).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/sensor/external/usage")
                        .contentType("application/json")
                        .header("X-USER-ID", "test-user")
                        .param("year", "2020")
                        .param("month", "March")
                        .param("bizCd", "A001")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.wholeCountry").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.kimCity").exists());
    }
}
