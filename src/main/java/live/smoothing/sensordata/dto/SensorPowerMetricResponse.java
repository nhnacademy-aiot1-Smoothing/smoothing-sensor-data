package live.smoothing.sensordata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SensorPowerMetricResponse {
    List<SensorPowerMetric> data;
}
