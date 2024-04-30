package live.smoothing.sensordata.util;

import com.influxdb.query.dsl.Flux;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.influxdb.query.dsl.functions.restriction.Restrictions.measurement;

public class FluxUtil {

    private FluxUtil() {}

    private static final String ROW_KEY = "_time";
    private static final String COLUMN_KEY = "_field";
    private static final String COLUMN_VALUE = "_value";
    private static final String FUNCTION = "({ r with value: float(v: r.value)})";

    public static Flux getFlux(String bucketName,
                               String measurementName,
                               Instant start) {

        return Flux.from(bucketName)
                .range(start)
                .filter(measurement().equal(measurementName))
//                .last()
                .pivot()
                .withRowKey(new String[]{ROW_KEY})
                .withColumnKey(new String[]{COLUMN_KEY})
                .withValueColumn(COLUMN_VALUE)
                .map(FUNCTION)
                .timeShift(9L, ChronoUnit.HOURS);
    }
}
