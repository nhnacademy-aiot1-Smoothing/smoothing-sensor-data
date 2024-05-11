package live.smoothing.sensordata.dto.phase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ThreePhase {
    private List<Phase> top;
    private List<Phase> bottom;
}
