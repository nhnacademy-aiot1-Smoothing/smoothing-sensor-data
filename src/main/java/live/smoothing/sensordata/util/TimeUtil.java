package live.smoothing.sensordata.util;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * 시간 관련 유틸리티 클래스
 *
 * 해당 시간들은 모두 UTC 시간에 맞춰서 반환된다.
 *
 * @author 박영준
 */
public class TimeUtil {

    private TimeUtil() {}

    /**
     * 단위 기준 최근 시간을 반환한다.
     *
     * @param source 기준 시간
     * @param offset 정각 기준 시간 단위
     * @return 시간
     */
    public static Instant getRecentHour(Instant source, long offset) {
        int hourOfDay = source.atZone(ZoneId.systemDefault()).getHour();
        long truncatedHour = hourOfDay / offset * offset;

        return source.truncatedTo(ChronoUnit.DAYS)
                .plus(truncatedHour, ChronoUnit.HOURS)
                .minus(9L, ChronoUnit.HOURS);
    }

    /**
     * 단위 기준 최근 분을 반환한다.
     *
     * @param source 기준 시간
     * @param offset 정각 기준 분 단위
     * @return 시간
     */
    public static Instant getRecentMinute(Instant source, long offset) {
        int minuteOfHour = source.atZone(ZoneId.systemDefault()).getMinute();
        long truncatedMinute = minuteOfHour / offset * offset;

        return source.truncatedTo(ChronoUnit.HOURS)
                .plus(truncatedMinute, ChronoUnit.MINUTES);
    }

    /**
     * 단위 기준 최근 월을 반환한다.
     *
     * @param source 기준 시간
     * @param offset 정각 기준 월 단위
     * @return 시간
     */
    public static Instant getRecentMonth(Instant source, int offset) {
        return ZonedDateTime.ofInstant(source, ZoneId.systemDefault())
                .minusMonths(offset-1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS).toInstant();
    }

    /**
     * 오늘 일자 자정을 반환한다.
     *
     * @param source 기준 시간
     * @return 시간
     */
    public static Instant getRecentDay(Instant source) {
        return source
                .plus(9L, ChronoUnit.HOURS)
                .truncatedTo(ChronoUnit.DAYS)
                .minus(9L, ChronoUnit.HOURS);
    }
}
