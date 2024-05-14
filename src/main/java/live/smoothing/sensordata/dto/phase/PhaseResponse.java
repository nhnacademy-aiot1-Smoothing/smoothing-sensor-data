package live.smoothing.sensordata.dto.phase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PhaseResponse {

    ThreePhase first;
    ThreePhase second;
    ThreePhase third;
}
