package live.smoothing.sensordata.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import live.smoothing.sensordata.adapter.openApi.KEPCOEnergyUsageApiAdapter;
import live.smoothing.sensordata.dto.usage.EnergyUsage;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

class UsageServiceImplTest {

    @Mock
    private KEPCOEnergyUsageApiAdapter apiAdapter;

    @InjectMocks
    private UsageServiceImpl usageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용량-서비스-정상응답-테스트")
    void getEnergyUsageValidResponseTest() {
        // Given
        int year = 2024;
        String month = "04";
        String bizCd = "123";
        EnergyUsageResponse expectedResponse = new EnergyUsageResponse();
        List<EnergyUsage> usageList = new ArrayList<>();
        EnergyUsage usage = new EnergyUsage();
        usage.setYear("2024");
        usage.setMonth("04");
        usage.setMetro("Seoul");
        usage.setCity("Gangnam");
        usage.setBiz("Retail");
        usage.setCustomerCount("100");
        usage.setPowerUsage("5000");
        usage.setBill("150000");
        usage.setUnitCost("30");
        usageList.add(usage);
        expectedResponse.setData(usageList);

        when(apiAdapter.fetchEnergyUsage(year, month, bizCd)).thenReturn(expectedResponse);

        // When
        EnergyUsageResponse result = usageService.getEnergyUsage(year, month, bizCd);

        // Then
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty());
        assertEquals("2024", result.getData().get(0).getYear());
        assertEquals("5000", result.getData().get(0).getPowerUsage());
        verify(apiAdapter, times(1)).fetchEnergyUsage(year, month, bizCd);
    }

    @Test
    @DisplayName("사용량-서비스-에러응답-테스트")
    void getEnergyUsage_throwsException_whenAdapterFails() {
        int year = 2024;
        String month = "05";
        String bizCd = "ABC";
        when(apiAdapter.fetchEnergyUsage(year, month, bizCd))
                .thenThrow(new RuntimeException("Failed to fetch data"));

        assertThrows(RuntimeException.class, () -> usageService.getEnergyUsage(year, month, bizCd));
    }
}