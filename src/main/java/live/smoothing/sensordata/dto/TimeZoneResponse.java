package live.smoothing.sensordata.dto;

import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 시간대 별 Kwh 데이터를 담고 있는 응답 클래스
 *
 * @author 신민석
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeZoneResponse {
    List<KwhTimeZoneResponse> data;
}
