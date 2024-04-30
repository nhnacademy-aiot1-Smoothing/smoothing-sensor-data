package live.smoothing.sensordata.util;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
