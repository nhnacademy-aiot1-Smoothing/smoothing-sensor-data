package live.smoothing.sensordata.dto.watt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 전력 관련 응답 DTO
 *
 * @author 박영준
 */
@Getter
@AllArgsConstructor
public class PowerMetricResponse {

    private List<String> tags;
    private List<PowerMetric> data;
}
