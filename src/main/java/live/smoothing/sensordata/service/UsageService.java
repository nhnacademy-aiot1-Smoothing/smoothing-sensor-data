package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;

public interface UsageService {

    EnergyUsageResponse getEnergyUsage(int year, String month, String bizCd);
}
