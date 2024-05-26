package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.annotation.DynamicAggCacheable;
import live.smoothing.sensordata.annotation.DynamicRawCacheable;
import live.smoothing.sensordata.annotation.StaticPeriodCacheable;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static live.smoothing.sensordata.util.FluxQuery.*;

/**
 * InfluxDB를 이용한 SeriesRepository 구현체
 *
 * @author  박영준
 */
@Repository
@RequiredArgsConstructor
public class SeriesRepositoryImpl implements SeriesRepository {

    private final InfluxDBClient rawInfluxClient;
    private final InfluxDBClient aggregationInfluxClient;

    /**
     * {@inheritDoc}
     */
    @DynamicRawCacheable
    @Override
    public List<Point> getStartData(String bucket, String measurement, Instant start, String[] topics) {

        Flux firstQuery = fetchFirstDataFromStart(
                bucket,
                measurement,
                start,
                topics
        );

        return rawInfluxClient.getQueryApi().query(firstQuery.toString(), Point.class);
    }

    /**
     * {@inheritDoc}
     */
    @DynamicRawCacheable
    @Override
    public List<Point> getEndData(String bucket, String measurement, Instant start, String[] topics) {

        Flux lastQuery = fetchLastDataFromStart(
                bucket,
                measurement,
                start,
                topics
        );

        return rawInfluxClient.getQueryApi().query(lastQuery.toString(), Point.class);
    }

    /**
     * {@inheritDoc}
     */
    @StaticPeriodCacheable
    @Override
    public List<Point> getDataByPeriod(String bucket, String measurement, Instant start, Instant end, String[] topics) {
        Flux query =
                fetchDataFromStart(
                        bucket,
                        measurement,
                        start,
                        end,
                        topics
                );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Point.class);
    }

    /**
     * {@inheritDoc}
     */
    @DynamicAggCacheable
    @Override
    public List<Point> getDataFromStart(String bucket, String measurement, Instant start, String[] topics) {
        Flux query = fetchDataFromStart(
                bucket,
                measurement,
                start,
                Instant.now(),
                topics
        );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Point.class);
    }

    /**
     * {@inheritDoc}
     */
    @DynamicRawCacheable
    @Override
    public List<Point> getSumDataFromStart(String bucket, String measurement, Instant start, String[] topics) {
        Flux query = fetchSumDataFromStart(
                bucket,
                measurement,
                start,
                topics
        );

        return rawInfluxClient.getQueryApi().query(query.toString(), Point.class);
    }
}