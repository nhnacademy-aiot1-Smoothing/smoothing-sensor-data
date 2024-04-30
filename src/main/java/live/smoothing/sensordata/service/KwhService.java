package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.PowerMetricResponse;

public interface KwhService {

    PowerMetricResponse get24HourData();
}
