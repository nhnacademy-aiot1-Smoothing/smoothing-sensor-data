package live.smoothing.sensordata.dto.topic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TopicResponse {
    List<String> topics;
}
