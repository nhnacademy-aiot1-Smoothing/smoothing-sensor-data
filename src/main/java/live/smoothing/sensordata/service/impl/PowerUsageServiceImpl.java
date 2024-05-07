package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.statistics.PowerUsageStatisticsResponse;
import live.smoothing.sensordata.repository.StatisticsRepository;
import live.smoothing.sensordata.service.PowerUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 *
 *
 * @author 신민석
 */
@Service
@RequiredArgsConstructor
public class PowerUsageServiceImpl implements PowerUsageService {

    private final StatisticsRepository repository;

    @Override
    public PowerUsageStatisticsResponse getPowerUsageStats(String measurement,
                                                           Instant start,
                                                           Instant end,
                                                           String[] topics) {

        var week = repository.getPowerUsageData(measurement, start, end, topics, "1w");
        var month = repository.getPowerUsageData(measurement, start, end, topics, "1mo");
        var year = repository.getPowerUsageData(measurement, start, end, topics, "1y");

        return new PowerUsageStatisticsResponse(week.getValues(), month.getValues(), year.getValues());
    }
}