package live.smoothing.sensordata.dto.usage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnergyUsageResponse {

    @JsonProperty("data")
    List<EnergyUsage> data;
}
