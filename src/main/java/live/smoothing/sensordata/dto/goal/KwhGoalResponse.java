package live.smoothing.sensordata.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KwhGoalResponse {

    private Double goalAmount;
    private Double currentAmount;
}
