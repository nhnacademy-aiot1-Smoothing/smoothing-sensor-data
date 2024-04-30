package live.smoothing.sensordata.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    private TimeUtil() {}

    public static Instant getRecentHour(Instant source, long offset) {
        int hourOfDay = source.atZone(ZoneId.systemDefault()).getHour();
        long truncatedHour = hourOfDay / offset * offset;

        return source.truncatedTo(ChronoUnit.DAYS)
                .plus(truncatedHour, ChronoUnit.HOURS)
                .minus(9L, ChronoUnit.HOURS);
    }

    public static Instant getRecentMinute(Instant source, long offset) {
        int minuteOfHour = source.atZone(ZoneId.systemDefault()).getMinute();
        long truncatedMinute = minuteOfHour / offset * offset;

        return source.truncatedTo(ChronoUnit.HOURS)
                .plus(truncatedMinute, ChronoUnit.MINUTES);
    }

    public static void main(String[] args) {
        System.out.println(getRecentHour(Instant.now(), 1));
        System.out.println(getRecentMinute(Instant.now(), 10));
    }
}
