package live.smoothing.sensordata.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PowerUsageStatisticsResponse {
    private List<Double> weekUsage;
    private List<Double> monthUsage;
    private List<Double> yearUsage;
}
