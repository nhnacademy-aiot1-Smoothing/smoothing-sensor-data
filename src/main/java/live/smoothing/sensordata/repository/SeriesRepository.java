package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Point;

import java.time.Instant;
import java.util.List;

/**
 * 사용전력량 Repository
 *
 * @author 박영준
 */
public interface SeriesRepository {

    /**
     * InfluxDB를 조회하여 시작 시간 기준으로 처음 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 포인트 리스트
     */
    List<Point> getStartData(String bucket, String measurement, Instant start, String[] topics);

    /**
     * InfluxDB를 조회하여 시작 시간 기준으로 마지막 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 포인트 리스트
     */
    List<Point> getEndData(String bucket, String measurement, Instant start, String[] topics);

    /**
     * InfluxDB를 조회하여 시작 시간과 종료 시간 사이의 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param end 종료 시간
     * @param topics 조회할 topic
     * @return 포인트 리스트
     */
    List<Point> getDataByPeriod(String bucket, String measurement,  Instant start, Instant end, String[] topics);

    /**
     * InfluxDB를 조회하여 시작 시간부터 현재 시간까지의 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 포인트 리스트
     */
    List<Point> getDataFromStart(String bucket, String measurement, Instant start, String[] topics);

    /**
     * InfluxDB를 조회하여 시작 시간부터의 합계 값을 반환
     *
     * @param bucket 버킷 이름
     * @param measurement 측정값 이름
     * @param start 시작 시간
     * @param topics 조회할 topic
     * @return 포인트 리스트
     */
    List<Point> getSumDataFromStart(String bucket, String measurement, Instant start, String[] topics);
}
