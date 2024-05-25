package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.TagPowerMetricResponse;

public interface WattService {

    TagPowerMetricResponse get10MinuteWattData(String tags);
    TagPowerMetricResponse get1HourWattData(String tags);
}
