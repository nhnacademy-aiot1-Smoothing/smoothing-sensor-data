package live.smoothing.sensordata.prop;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "influxdb.aggregation")
public class AggregationInfluxDBProperties {
    private String url;
    private String token;
    private String org;
    private String bucket;
}
