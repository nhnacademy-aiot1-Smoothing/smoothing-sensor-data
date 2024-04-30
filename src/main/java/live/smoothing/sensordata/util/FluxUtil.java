package live.smoothing.sensordata.util;

import com.influxdb.query.dsl.Flux;

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
     * @param topics 토픽
     * @return Flux 쿼리
     */
    public static Flux getFlux(String bucketName,
                               String measurementName,
                               Instant start,
                               String[] topics
    ) {
        return Flux.from(bucketName)
                .range(start)
                .filter(measurement().equal(measurementName))
//                .filter(Restrictions.tag("topic").contains(topics))
                .pivot()
                .withRowKey(new String[]{ROW_KEY})
                .withColumnKey(new String[]{COLUMN_KEY})
                .withValueColumn(COLUMN_VALUE)
                .map(FUNCTION)
                .timeShift(9L, ChronoUnit.HOURS);
    }
}
