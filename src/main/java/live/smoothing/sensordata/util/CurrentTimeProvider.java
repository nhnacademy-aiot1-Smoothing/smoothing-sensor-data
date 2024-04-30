package live.smoothing.sensordata.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrentTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
