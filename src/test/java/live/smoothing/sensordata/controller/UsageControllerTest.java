package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.usage.EnergyUsage;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import live.smoothing.sensordata.service.UsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsageController.class)
class UsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsageService usageService;

    @BeforeEach

    @Test
    @DisplayName("에너지 사용 데이터를 요청 응답 테스트")
    void getEnergyUsage_ValidResponseTest() throws Exception {
        // given
        int year = 2024;
        String month = "04";
        String bizCd = "123";

        EnergyUsage energyUsage = new EnergyUsage();

        energyUsage.setYear("2024");
        energyUsage.setMonth("04");
        energyUsage.setMetro("경남");
        energyUsage.setCity("김해");
        energyUsage.setBiz("Retail");
        energyUsage.setCustomerCount("100");
        energyUsage.setPowerUsage("5000");
        energyUsage.setBill("150000");
        energyUsage.setUnitCost("30");

        List<EnergyUsage> usageList = Arrays.asList(
                energyUsage);
        EnergyUsageResponse expectedResponse = new EnergyUsageResponse();
        expectedResponse.setData(usageList);

        given(usageService.getEnergyUsage(year, month, bizCd)).willReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/external/usage")
                        .param("year", String.valueOf(year))
                        .param("month", month)
                        .param("bizCd", bizCd)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].year").value("2024"))
                .andExpect(jsonPath("$.data[0].month").value("04"))
                .andExpect(jsonPath("$.data[0].metro").value("경남"))
                .andExpect(jsonPath("$.data[0].city").value("김해"))
                .andExpect(jsonPath("$.data[0].biz").value("Retail"))
                .andExpect(jsonPath("$.data[0].custCnt").value("100"))
                .andExpect(jsonPath("$.data[0].powerUsage").value("5000"))
                .andExpect(jsonPath("$.data[0].bill").value("150000"))
                .andExpect(jsonPath("$.data[0].unitCost").value("30"));
    }

    @Test
    @DisplayName("요청한 에너지사용 데이터가 없는 경우 404 응답")
    void getEnergyUsage_returnsNotFound() throws Exception {
        // given
        int year = 2024;
        String month = "05";
        String bizCd = "ABC";
        given(usageService.getEnergyUsage(year, month, bizCd)).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/external/usage")
                        .param("year", String.valueOf(year))
                        .param("month", month)
                        .param("bizCd", bizCd)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}