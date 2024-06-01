package live.smoothing.sensordata.adapter;

import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;

public interface EnergyUsageApiAdapter {

    EnergyUsageResponse fetchEnergyUsage(int year, String month, String bizCd);
}
