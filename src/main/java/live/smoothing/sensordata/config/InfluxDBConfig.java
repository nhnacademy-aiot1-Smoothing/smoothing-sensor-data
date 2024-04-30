package live.smoothing.sensordata.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import live.smoothing.sensordata.prop.AggregationInfluxDBProperties;
import live.smoothing.sensordata.prop.RawInfluxDBProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InfluxDBConfig {

    private final AggregationInfluxDBProperties aggregationInfluxDBProperties;
    private final RawInfluxDBProperties rawInfluxDBProperties;

    @Bean
    public InfluxDBClient aggregationInfluxClient() {
        return InfluxDBClientFactory
                .create(
                        aggregationInfluxDBProperties.getUrl(),
                        aggregationInfluxDBProperties.getToken().toCharArray(),
                        aggregationInfluxDBProperties.getOrg()
                );
    }

    @Bean
    public InfluxDBClient rawInfluxClient() {
        return InfluxDBClientFactory
                .create(
                        rawInfluxDBProperties.getUrl(),
                        rawInfluxDBProperties.getToken().toCharArray(),
                        rawInfluxDBProperties.getOrg()
                );
    }
}
