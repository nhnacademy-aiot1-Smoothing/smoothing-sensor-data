package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.openApi.KEPCOEnergyUsageApiAdapter;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import live.smoothing.sensordata.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {

    private final KEPCOEnergyUsageApiAdapter apiAdapter;

    @Autowired
    public UsageServiceImpl(KEPCOEnergyUsageApiAdapter apiAdapter) {
        this.apiAdapter = apiAdapter;
    }

    @Override
    public EnergyUsageResponse getEnergyUsage(int year, String month, String bizCd) {
        return apiAdapter.fetchEnergyUsage(year, month, bizCd);
    }
}
