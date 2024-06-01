package live.smoothing.sensordata.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class GoalResponse {
    private Double goalAmount;
    private Integer unitPrice;
}