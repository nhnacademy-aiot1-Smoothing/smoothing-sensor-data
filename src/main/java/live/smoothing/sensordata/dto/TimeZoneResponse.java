package live.smoothing.sensordata.dto;

import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TimeZoneResponse {
    List<KwhTimeZoneResponse> data;
}
