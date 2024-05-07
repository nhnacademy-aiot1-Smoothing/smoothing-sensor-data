package live.smoothing.sensordata.util;

import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.influxdb.query.dsl.functions.restriction.Restrictions.measurement;

/**
 * InfluxDB 쿼리를 생성하는 유틸리티 클래스
 *
 * @author 신민석
 */
public class FluxUtil {

    private FluxUtil() {}

    private static final String ROW_KEY = "_time";
    private static final String COLUMN_KEY = "_field";
    private static final String COLUMN_VALUE = "_value";
    private static final String FUNCTION = "({ r with value: float(v: r.value)})";

    /**
     * InfluxDB Flux 쿼리를 생성한다.
     *
     * @param bucketName 버킷 이름
     * @param measurementName 측정값 이름
     * @param start 시작 시간
     * @param end 종료 시간
     * @param topics 토픽
     * @return Flux 쿼리
     */
    public static Flux getKwhFromStart(String bucketName,
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
                .pivot()
                .withRowKey(new String[]{ROW_KEY})
                .withColumnKey(new String[]{COLUMN_KEY})
                .withValueColumn(COLUMN_VALUE)
                .map(FUNCTION)
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
    public static Flux getFirstKwhFromStart(String bucketName,
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
    public static Flux getLastKwhFromStart(String bucketName,
                                           String measurementName,
                                           Instant start,
                                           String[] topics
    ) {
        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start)
                .filter(measurement().equal(measurementName))
                .filter(orRestrictions)
                .map("({ r with _time: time(v: now())})")
                .last()
                .timeShift(9L, ChronoUnit.HOURS);
    }

    public static Flux getWattSumFromStart(String bucketName,
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
                .map("({ r with _time: time(v: now())})")
                .timeShift(9L, ChronoUnit.HOURS);
    }

    public static Flux getAggregationWattFromStart(String bucketName,
                                                   String measurementName,
                                                   Instant start,
                                                   String[] topics
    ) {
        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start)
                .filter(Restrictions.measurement().equal(measurementName))
                .filter(orRestrictions)
                .timeShift(9L, ChronoUnit.HOURS);
    }

    //Todo: 고쳐야함
    public static Flux getAggregatedPowerUsage(String bucketName,
                                               String measurementName,
                                               Instant start,
                                               Instant end,
                                               String period,
                                               String[] topics) {

        Restrictions orRestrictions = getOrRestrictions(topics);

        return Flux.from(bucketName)
                .range(start, end)
                .filter(measurement().equal(measurementName))
                .filter(orRestrictions)
                .window() //데이터를 시간별로 그루핑해줌
                .withEvery(period) // 1mo, 1w, 1y 넣어주면 댐
                .sum(); // 그룹 테이터를 합쳐줌
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
