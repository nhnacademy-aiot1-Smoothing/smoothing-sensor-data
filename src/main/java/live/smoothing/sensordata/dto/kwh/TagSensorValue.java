package live.smoothing.sensordata.dto.kwh;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagSensorValue {

    String sensorName;
    Double value;
}
