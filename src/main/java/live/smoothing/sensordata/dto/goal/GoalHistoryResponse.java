package live.smoothing.sensordata.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GoalHistoryResponse {

    private LocalDateTime date;
    private Double goalAmount;
    @Setter
    private Double amount;
}
