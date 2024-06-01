package live.smoothing.sensordata.dto.topic;

import lombok.Getter;

import java.util.List;

@Getter
public class SensorTopicResponse {
    private List<SensorWithTopic> sensorWithTopics;
}
