package live.smoothing.sensordata.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalRequest {
    private Long goalAmount;
    private Integer unitPrice;
}
