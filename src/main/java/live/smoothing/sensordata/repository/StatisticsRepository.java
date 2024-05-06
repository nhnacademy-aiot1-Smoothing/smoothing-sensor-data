package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Kwh;

import java.time.Instant;
import java.util.List;

public interface StatisticsRepository {

    List<Kwh> getPowerUsageData(String measurement, Instant start, Instant end, String[] topics);
}
