package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static live.smoothing.sensordata.util.FluxQuery.*;

/**
 * InfluxDB를 이용한 KwhRepository 구현체
 *
 * @author  박영준
 */
@Repository
@RequiredArgsConstructor
public class SeriesRepositoryImpl implements SeriesRepository {

    private final InfluxDBClient rawInfluxClient;
    private final InfluxDBClient aggregationInfluxClient;

    /**
     * InfluxDB를 조회하여 시작 시간 기준으로 처음 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 전력량 리스트
     */
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
     * InfluxDB를 조회하여 시작 시간 기준으로 마지막 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 전력량 리스트
     */
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
     * InfluxDB를 조회하여 시작 시간과 종료 시간 사이의 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param end 종료 시간
     * @param topics 조회할 topic
     * @return 전력량 리스트
     */
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
     * InfluxDB를 조회하여 시작 시간부터의 합계 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 전력량 리스트
     */
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