package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.repository.KwhRepository;
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
    public List<Kwh> get24HourData() {
        Flux query =
                getFlux(
                        AGGREGATION_BUCKET,
                        "kwh_hour",
                        Instant.now().minus(1, ChronoUnit.DAYS)
                );

        try(InfluxDBClient influxDBClient = client.aggregationInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> getWeekData() {
        Flux query =
                getFlux(
                        AGGREGATION2_BUCKET,
                        "kwh_daily",
                        Instant.now()
                );

        try(InfluxDBClient influxDBClient = client.aggregationInfluxClient()) {

            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> get24Raw() {

        Flux query =
                getFlux(
                        RAW_BUCKET,
                        "mqtt_consumer",
                        Instant.now().minus(1L, ChronoUnit.HOURS)
                );

        try(InfluxDBClient influxDBClient = client.rawInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> getWeekRaw(String measurementName) {

        Flux query =
                getFlux(
                        AGGREGATION2_BUCKET,
                        measurementName,
                        Instant.now().minus(1L, ChronoUnit.DAYS)
                );

        try(InfluxDBClient influxDBClient = client.rawInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }
}
