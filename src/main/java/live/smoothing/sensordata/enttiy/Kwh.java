package live.smoothing.sensordata.enttiy;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Kwh DTO
 *
 * @author 신민석
 */
@Getter
@Setter
public class Kwh {
    private Instant time;
    private String device;
    private String place;
    private String topic;
    private Double value;
}
