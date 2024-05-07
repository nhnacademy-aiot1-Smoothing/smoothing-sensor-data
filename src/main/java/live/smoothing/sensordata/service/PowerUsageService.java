package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.statistics.PowerUsageStatisticsResponse;

import java.time.Instant;

/**
 * @author 신민석
 */
public interface PowerUsageService {

    PowerUsageStatisticsResponse getPowerUsageStats(String measurement, Instant start, Instant end, String[] topics);
}
