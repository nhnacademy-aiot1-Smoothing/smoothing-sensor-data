package live.smoothing.sensordata.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

/**
 * 전력 관련 DTO
 *
 * type : 전력 종류
 * unit : 시간 단위 (ex : min, hour)
 * per : 시간 단위당 값
 * time : 시간
 * value : 값
 *
 * @author 박영준
 */
@Getter
@RequiredArgsConstructor
public class PowerMetric {

    private final String type;
    private final String unit;
    private final String per;
    private final Instant time;
    private final Double value;
}
