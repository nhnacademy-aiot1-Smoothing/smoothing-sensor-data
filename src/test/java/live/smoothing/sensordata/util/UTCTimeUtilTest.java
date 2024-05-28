package live.smoothing.sensordata.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class UTCTimeUtilTest {

    @Test
    @DisplayName("현재 시간을 기준으로 offset 기준 최근 분을 가져온다.")
    void getRecentMinute() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 22, 10, 7);
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);

        // when
        Instant result = UTCTimeUtil.getRecentMinute(instant, 10);

        // then
        assertEquals("2024-05-22T10:00:00Z", result.toString());
    }

    @Test
    @DisplayName("현재 시간을 기준으로 최근 시간을 가져온다.")
    void getRecentHour() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 22, 2, 7);
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);

        // when
        Instant result = UTCTimeUtil.getRecentHour(instant);

        // then
        assertEquals("2024-05-22T02:00:00Z", result.toString());
    }

    @Test
    @DisplayName("현재 시간을 기준으로 UTC 기준 오늘 자정을 가져온다.")
    void getRecentDay() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 22, 10, 7);
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);

        // when
        Instant result = UTCTimeUtil.getRecentDay(instant);

        // then
        assertEquals("2024-05-21T15:00:00Z", result.toString());
    }

    @Test
    @DisplayName("현재 시간을 UTC 기준으로 최근 월을 가져온다.")
    void getRecentMonth() {
        // given
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 22, 10, 7);
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);

        // when
        Instant result = UTCTimeUtil.getRecentMonth(instant);

        // then
        assertEquals("2024-04-30T15:00:00Z", result.toString());
    }
}