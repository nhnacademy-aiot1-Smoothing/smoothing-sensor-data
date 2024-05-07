package live.smoothing.sensordata.dto.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorTopicResponse {
    private List<SensorWithTopic> sensorWithTopics;
}
