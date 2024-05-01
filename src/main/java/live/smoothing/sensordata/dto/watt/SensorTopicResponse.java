package live.smoothing.sensordata.dto.watt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SensorTopicResponse {
    List<String> topics;
}
