package live.smoothing.sensordata.dto.kwh;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TagSensorValueResponse {

    private List<TagSensorValue> data;
}
