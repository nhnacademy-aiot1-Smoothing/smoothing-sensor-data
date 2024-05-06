package live.smoothing.sensordata.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PowerUsageStatisticsResponse {
    private List<Double> weeklyUsage;
    private List<Double> monthlyUsage;
    private List<Double> yearlyUsage;
}
