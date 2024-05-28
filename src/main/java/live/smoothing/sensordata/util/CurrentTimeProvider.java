package live.smoothing.sensordata.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 현재 시간을 제공 클래스 구현체
 *
 * @author 박영준
 */
@Component
public class CurrentTimeProvider implements TimeProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant nowInstant() {
        return now().toInstant(ZoneOffset.of("+09:00"));
    }
}
