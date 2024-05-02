package live.smoothing.sensordata.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class CurrentTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public Instant nowInstant() {
        return now().toInstant(ZoneOffset.of("+09:00"));
    }
}
