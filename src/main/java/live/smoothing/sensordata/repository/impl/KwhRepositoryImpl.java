package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.repository.KwhRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.influxdb.query.dsl.functions.restriction.Restrictions.*;

@Service
@RequiredArgsConstructor
public class KwhRepositoryImpl implements KwhRepository {

    private final InfluxDBConfig client;

    private static final String AGGREGATION_BUCKET = "aggregation";
    private static final String AGGREGATION2_BUCKET = "aggregation2";
    private static final String RAW_BUCKET = "raw";
    private static final String ROW_KEY = "_time";
    private static final String COLUMN_KEY = "_field";
    private static final String COLUMN_VALUE = "_value";

    @Override
    public List<Kwh> get24HourData(Long start, ChronoUnit chronoUnit) {

        Flux query = Flux.from(AGGREGATION_BUCKET)
                .range(start, chronoUnit)
                .filter(measurement().equal("kwh_hour"))
                .last()
                .pivot()
                .withRowKey(new String[]{ROW_KEY})
                .withColumnKey(new String[]{COLUMN_KEY})
                .withValueColumn(COLUMN_VALUE)
                .map("({ r with value: float(v: r.value)})")
                .timeShift(9L, ChronoUnit.HOURS);

        try(InfluxDBClient influxDBClient = client.aggregationInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }

    @Override
    public List<Kwh> getWeekData(Long start, ChronoUnit chronoUnit) {
        Flux query = Flux.from(AGGREGATION2_BUCKET)
                .range(-7L, ChronoUnit.DAYS)
                .filter(measurement().equal("kwh_hour"))
                .last()
                .pivot()
                .withRowKey(new String[]{ROW_KEY})
                .withColumnKey(new String[]{COLUMN_KEY})
                .withValueColumn(COLUMN_VALUE)
                .map("({ r with value: float(v: r.value)})")
                .timeShift(9L, ChronoUnit.HOURS);

        try(InfluxDBClient influxDBClient = client.aggregationInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }


    public List<Kwh> getRaw(String bucketName, String measurementName, long range, ChronoUnit chronoUnit) {
        Flux query = Flux.from(bucketName)
                .range(range, chronoUnit)
                .filter(measurement().equal(measurementName))
                .last()
                .pivot()
                .withRowKey(new String[]{ROW_KEY})
                .withColumnKey(new String[]{COLUMN_KEY})
                .withValueColumn(COLUMN_VALUE)
                .map("({ r with value: float(v: r.value)})")
                .timeShift(9L, ChronoUnit.HOURS);

        try(InfluxDBClient influxDBClient = client.rawInfluxClient()) {
            return influxDBClient.getQueryApi().query(query.toString(), Kwh.class);
        }
    }
}
