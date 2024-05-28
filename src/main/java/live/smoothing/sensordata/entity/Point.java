package live.smoothing.sensordata.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
public class Point {

    @Setter
    private Instant time;
    private String topic;
    private String place;
    private Double value;
    private String description;
}
