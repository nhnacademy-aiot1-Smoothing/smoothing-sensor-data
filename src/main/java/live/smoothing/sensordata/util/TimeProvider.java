package live.smoothing.sensordata.util;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 시간 관련 유틸리티 클래스
 *
 * @author 박영준
 */
public interface TimeProvider {

    /**
     * LocalDateTime 타입으로 현재 시간을 반환한다.
     *
     * @return 현재 시간
     */
    LocalDateTime now();

    /**
     * Instant 타입으로 현재 UTC 시간을 반환한다.
     *
     * @return 현재 UTC 시간
     */
    Instant nowInstant();
}
