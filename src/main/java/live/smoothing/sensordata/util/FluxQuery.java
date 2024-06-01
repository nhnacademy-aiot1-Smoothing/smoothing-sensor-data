package live.smoothing.sensordata.util;

import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.influxdb.query.dsl.functions.restriction.Restrictions.measurement;

/**
 * InfluxDB 쿼리를 생성하는 유틸리티 클래스
 *
 * @author 박영준, 신민석
 */
public class FluxQuery {

    private FluxQuery() {}

    /**
     * 특정 범위 시간대의 값을 가져오는 InfluxDB FLux 쿼리를 생성한다.
     *
     * @param bucketName 버킷 이름
     * @param measurementName 측정값 이름
     * @param start 시작 시간
     * @param topics 토픽
     * @return Flux 쿼리
     */
    public static Flux fetchDataFromStart(String bucketName,
                                          String measurementName,
                                          Instant start,
                                          Instant end,
                                          String[] topics
    ) {
        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start, end)
                .filter(measurement().equal(measurementName))
                .filter(orRestrictions)
                .timeShift(9L, ChronoUnit.HOURS);
    }

    /**
     * 시작 시간 기준 가장 첫번째 값을 가져오는 InfluxDB FLux 쿼리를 생성한다.
     *
     * @param bucketName 버킷 이름
     * @param measurementName 측정값 이름
     * @param start 시작 시간
     * @param topics 토픽
     * @return Flux 쿼리
     */
    public static Flux fetchFirstDataFromStart(String bucketName,
                                               String measurementName,
                                               Instant start,
                                               String[] topics
    ) {
        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start)
                .filter(measurement().equal(measurementName))
                .filter(orRestrictions)
                .first()
                .timeShift(9L, ChronoUnit.HOURS);
    }

    /**
     * 시작 시간 기준 가장 마지막 값을 가져오는 InfluxDB FLux 쿼리를 생성한다.
     *
     * @param bucketName 버킷 이름
     * @param measurementName 측정값 이름
     * @param start 시작 시간
     * @param topics 토픽
     * @return Flux 쿼리
     */
    public static Flux fetchLastDataFromStart(String bucketName,
                                              String measurementName,
                                              Instant start,
                                              String[] topics
    ) {
        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start)
                .filter(measurement().equal(measurementName))
                .filter(orRestrictions)
                .last()
                .timeShift(9L, ChronoUnit.HOURS);
    }

    /**
     * 시작 시간 기준 합계 값을 가져오는 InfluxDB FLux 쿼리를 생성한다.
     *
     * @param bucketName 버킷 이름
     * @param measurementName 측정값 이름
     * @param start 시작 시간
     * @param topics 토픽
     * @return Flux 쿼리
     */
    public static Flux fetchSumDataFromStart(String bucketName,
                                             String measurementName,
                                             Instant start,
                                             String[] topics
    ) {
        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start)
                .filter(Restrictions.measurement().equal(measurementName))
                .filter(orRestrictions)
                .sum()
                .timeShift(9L, ChronoUnit.HOURS);
    }

    /**
     * 토픽을 OR 연산으로 연결하는 Restrictions를 생성한다.
     *
     * @param topics 토픽
     * @return Restrictions
     */
    private static Restrictions getOrRestrictions(String[] topics) {
        Restrictions restrictions = Restrictions.tag("topic").equal(topics[0]);

        for (int i = 1; i < topics.length; i++) {
            restrictions = Restrictions.or(restrictions, Restrictions.tag("topic").equal(topics[i]));
        }

        return restrictions;
    }
}
