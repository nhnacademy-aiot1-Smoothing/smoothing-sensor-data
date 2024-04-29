package live.smoothing.sensordata.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Kwh {
    private Instant time;
    private String device;
    private String place;
    private String topic;
    private Double value;

}
