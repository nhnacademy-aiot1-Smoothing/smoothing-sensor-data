package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.entity.Watt;
import live.smoothing.sensordata.repository.WattRepository;
import live.smoothing.sensordata.util.FluxUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WattRepositoryImpl implements WattRepository {

    private final InfluxDBClient rawInfluxClient;
    private final InfluxDBClient aggregationInfluxClient;

    private static final String RAW_BUCKET_NAME = "powermetrics_data";
    private static final String AGGREGATION_BUCKET_NAME = "aggregation";

    @Override
    public List<Watt> getRawWattData(Instant start, String[] topics, String measurement) {

        Flux flux = FluxUtil.getWattSumFromStart(
                RAW_BUCKET_NAME,
                measurement,
                start,
                topics
        );

        return rawInfluxClient.getQueryApi().query(flux.toString(), Watt.class);
    }

    @Override
    public List<Watt> getAggregateWattData(Instant start, String[] topics, String measurement) {

        Flux flux = FluxUtil.getAggregationWattFromStart(
                AGGREGATION_BUCKET_NAME,
                measurement,
                start,
                topics
        );

        return aggregationInfluxClient.getQueryApi().query(flux.toString(), Watt.class);
    }
}