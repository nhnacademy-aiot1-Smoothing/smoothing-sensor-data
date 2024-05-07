package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Watt;

import java.time.Instant;
import java.util.List;

public interface WattRepository {

    List<Watt> getRawWattData(Instant start, String[] topic, String measurement);
    List<Watt> getAggregateWattData(Instant start, String[] topic, String measurement);
}
