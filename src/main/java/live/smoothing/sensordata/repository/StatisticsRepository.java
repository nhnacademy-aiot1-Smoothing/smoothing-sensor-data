package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.statistics.KwhTimeSeriesResponse;

import java.time.Instant;

public interface StatisticsRepository {

    KwhTimeSeriesResponse getPowerUsageData(String measurement, Instant start, Instant end, String[] topics, String period);
}
