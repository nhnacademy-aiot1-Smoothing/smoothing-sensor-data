package live.smoothing.sensordata.entity;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Measurement(name = "watt")
public class Watt {

    @Column(name = "time")
    private Instant time;

    @Column(name = "place")
    private String place;

    @Column(name = "location")
    private String location;

    @Column(name = "topic")
    private String topic;

    @Column(name = "value")
    private Double value;
}
