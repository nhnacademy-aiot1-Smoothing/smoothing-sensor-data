package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import live.smoothing.sensordata.dto.watt.Watt;
import live.smoothing.sensordata.repository.WattRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InfluxDBWattRepository implements WattRepository {

    private final InfluxDBClient rawInfluxClient;
    private final InfluxDBClient aggregationInfluxClient;

    private static final String RAW_BUCKET_NAME = "powermetrics_data";
    private static final String AGGREGATION_BUCKET_NAME = "aggregation";

    @Override
    public List<Watt> getRawWattData(Instant start, String[] topics, String measurement) {
        Flux flux = Flux.from(RAW_BUCKET_NAME)
                .range(start)
                .filter(Restrictions.measurement().equal(measurement))
                .filter(Restrictions.tag("topic").contains(topics))
                .sum()
                .map("({ r with _time: time(v: now())})")
                .timeShift(9L, ChronoUnit.HOURS);

        return rawInfluxClient.getQueryApi().query(flux.toString(), Watt.class);
    }

    @Override
    public List<Watt> getAggregateWattData(Instant start, String[] topics, String measurement) {
        Flux flux = Flux.from(AGGREGATION_BUCKET_NAME)
                .range(start)
                .filter(Restrictions.measurement().equal(measurement))
                .filter(Restrictions.tag("topic").contains(topics))
                .timeShift(9L, ChronoUnit.HOURS);

        return aggregationInfluxClient.getQueryApi().query(flux.toString(), Watt.class);
    }
}