package live.smoothing.sensordata.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CurrentTimeProviderTest {

    @Test
    void now() {
        TimeProvider timeProvider = new CurrentTimeProvider();
        assertAll(
                () -> assertThat(timeProvider.now()).isNotNull(),
                () -> assertThat(timeProvider.now()).isInstanceOf(LocalDateTime.class)
        );
    }

    @Test
    void nowInstant() {
        TimeProvider timeProvider = new CurrentTimeProvider();
        assertAll(
                () -> assertThat(timeProvider.nowInstant()).isNotNull(),
                () -> assertThat(timeProvider.nowInstant()).isInstanceOf(Instant.class)
        );
    }
}