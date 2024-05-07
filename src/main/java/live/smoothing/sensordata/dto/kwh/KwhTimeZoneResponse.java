package live.smoothing.sensordata.dto.kwh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KwhTimeZoneResponse {
    String label;
    Double value;
}
