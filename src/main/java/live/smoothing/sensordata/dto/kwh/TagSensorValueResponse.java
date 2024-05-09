package live.smoothing.sensordata.dto.kwh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagSensorValueResponse {

    private List<TagSensorValue> data;
}
