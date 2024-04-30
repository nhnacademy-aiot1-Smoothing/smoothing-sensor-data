package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static live.smoothing.sensordata.util.FluxUtil.getFlux;

@Service
@RequiredArgsConstructor
public class KwhRepositoryImpl implements KwhRepository {

    private final InfluxDBConfig client;
    private static final String AGGREGATION_BUCKET = "aggregation";
    private static final String AGGREGATION2_BUCKET = "aggregation2";
    private static final String RAW_BUCKET = "powermetrics_data";

    @Override
    public List<Kwh> get24HourData(String[] topics) {
        Flux query =
                getFlux(
                        AGGREGATION_BUCKET,
                        "kwh_hour",
                        Instant.now().minus(1, ChronoUnit.DAYS),
                        topics
                );

        try(InfluxDBClient influxDBClient = client.aggregationInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> getWeekData(String[] topics) {
        Flux query =
                getFlux(
                        AGGREGATION2_BUCKET,
                        "kwh_daily",
                        Instant.now(),
                        topics
                );

        try(InfluxDBClient influxDBClient = client.aggregationInfluxClient()) {

            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> get24Raw(String[] topics) {

        Flux query =
                getFlux(
                        RAW_BUCKET,
                        "mqtt_consumer",
                        TimeUtil.getRecentHour(Instant.now(), 1),
                        topics
                );

        try(InfluxDBClient influxDBClient = client.rawInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> getWeekRaw(String[] topics) {

        Flux query =
                getFlux(
                        AGGREGATION2_BUCKET,
                        "kwh_daily",
                        Instant.now().minus(1L, ChronoUnit.DAYS),
                        topics
                );

        try(InfluxDBClient influxDBClient = client.rawInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }
}
