package live.smoothing.sensordata.dto.usage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyUsageResponse {

    private EnergyUsage wholeCountry;
    private EnergyUsage kimCity;
}
