package live.smoothing.sensordata.dto;

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
public class TagPowerMetricResponse {

    private List<String> tags;
    private List<PowerMetric> data;
}
