package live.smoothing.sensordata.dto.phase;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PhaseResponse {

    ThreePhase classA;
    ThreePhase office;
}
