package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.watt.PowerMetricResponse;

public interface WattService {

    PowerMetricResponse get10MinuteWattData(String type, String unit, String per, String tags);
    PowerMetricResponse get1HourWattData(String type, String unit, String per, String tags);
}
