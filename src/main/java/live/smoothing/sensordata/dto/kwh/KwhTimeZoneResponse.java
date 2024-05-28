package live.smoothing.sensordata.dto.kwh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KwhTimeZoneResponse {
    String label;
    Double value;
}
