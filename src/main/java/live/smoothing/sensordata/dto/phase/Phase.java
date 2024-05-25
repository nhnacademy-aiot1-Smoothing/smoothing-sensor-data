package live.smoothing.sensordata.dto.phase;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Phase {
    private Instant time;
    private Double value;
}
