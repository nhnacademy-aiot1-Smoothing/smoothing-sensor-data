package live.smoothing.sensordata.dto.phase;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Phase {
    private Instant time;
    private Double value;
}
