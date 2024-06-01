package live.smoothing.sensordata.util;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * 시간 관련 유틸리티 클래스
 *
 * @author 박영준
 */
public class UTCTimeUtil {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private UTCTimeUtil() {}

    /**
     * 단위 기준 최근 분을 반환한다.
     *
     * @param source 기준 시간
     * @param offset 정각 기준 분 단위
     * @return 시간
     */
    public static Instant getRecentMinute(Instant source, long offset) {
        int minuteOfHour = source.atZone(KST_ZONE_ID).getMinute();
        long truncatedMinute = minuteOfHour / offset * offset;

        return source.atZone(KST_ZONE_ID)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(truncatedMinute, ChronoUnit.MINUTES)
                .toInstant();
    }

    /**
     * 가장 최근 시간을 반환한다.
     *
     * @param source 기준 시간
     * @return 시간
     */
    public static Instant getRecentHour(Instant source) {
        return source.atZone(KST_ZONE_ID).truncatedTo(ChronoUnit.HOURS).toInstant();
    }

    /**
     * 오늘 일자 자정을 반환한다.
     *
     * @param source 기준 시간
     * @return 시간
     */
    public static Instant getRecentDay(Instant source) {
        return source.atZone(KST_ZONE_ID)
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant();
    }

    /**
     * 가장 최근 월을 반환한다.
     *
     * @param source 기준 시간
     * @return 시간
     */
    public static Instant getRecentMonth(Instant source) {
        return ZonedDateTime.ofInstant(source, KST_ZONE_ID)
                .with(TemporalAdjusters.firstDayOfMonth())
                .truncatedTo(ChronoUnit.DAYS).toInstant();
    }
}
