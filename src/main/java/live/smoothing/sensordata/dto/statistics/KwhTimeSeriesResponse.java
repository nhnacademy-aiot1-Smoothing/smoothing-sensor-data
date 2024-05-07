package live.smoothing.sensordata.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KwhTimeSeriesResponse {
    private String label;
    private List<Double> values;
}
