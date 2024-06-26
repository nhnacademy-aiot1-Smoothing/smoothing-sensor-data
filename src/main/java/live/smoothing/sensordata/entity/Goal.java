package live.smoothing.sensordata.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "goal_date")
    private LocalDateTime goalDate;

    @Column(name = "goal_amount")
    private Double goalAmount;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "unit_price")
    private Integer unitPrice;
}

