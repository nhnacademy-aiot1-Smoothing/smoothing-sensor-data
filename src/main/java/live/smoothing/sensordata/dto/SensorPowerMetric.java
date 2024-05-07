package live.smoothing.sensordata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorPowerMetric {
    private String sensorName;
    private List<PowerMetric> powerMetrics;
}
