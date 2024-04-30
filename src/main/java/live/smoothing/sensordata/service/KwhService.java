package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.PowerMetricResponse;

public interface KwhService {

    PowerMetricResponse get24HourData(String type, String unit, String per, String tags);

    PowerMetricResponse getWeekData(String type, String unit, String per, String tags);
}
