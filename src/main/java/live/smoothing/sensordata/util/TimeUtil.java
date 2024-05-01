package live.smoothing.sensordata.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * 시간 관련 유틸리티 클래스
 *
 * @author 신민석
 */
public class TimeUtil {

    private TimeUtil() {}

    /**
     * 최근 시간을 반환한다.
     *
     * @param source 기준 시간
     * @param offset 시간 단위
     * @return 최근 시간
     */
    public static Instant getRecentHour(Instant source, long offset) {
        int hourOfDay = source.atZone(ZoneId.systemDefault()).getHour();
        long truncatedHour = hourOfDay / offset * offset;

        return source.truncatedTo(ChronoUnit.DAYS)
                .plus(truncatedHour, ChronoUnit.HOURS)
                .minus(9L, ChronoUnit.HOURS);
    }

    /**
     * 최근 분을 반환한다.
     *
     * @param source 기준 시간
     * @param offset 분 단위
     * @return 최근 분
     */
    public static Instant getRecentMinute(Instant source, long offset) {
        int minuteOfHour = source.atZone(ZoneId.systemDefault()).getMinute();
        long truncatedMinute = minuteOfHour / offset * offset;

        return source.truncatedTo(ChronoUnit.HOURS)
                .plus(truncatedMinute, ChronoUnit.MINUTES);
    }

    public static int getMonth(LocalDateTime localDateTime) {
        return localDateTime.getMonthValue();
    }
}
