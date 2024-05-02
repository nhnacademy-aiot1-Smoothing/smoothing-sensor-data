package live.smoothing.sensordata.dto;

import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeZoneResponse {
    List<KwhTimeZoneResponse> data;
}
