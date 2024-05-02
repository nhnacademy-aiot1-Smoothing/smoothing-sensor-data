package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.watt.PowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.entity.Watt;
import live.smoothing.sensordata.repository.WattRepository;
import live.smoothing.sensordata.service.WattService;
import live.smoothing.sensordata.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WattServiceImpl implements WattService {

    private static final String TOPIC_TYPE_NAME = "전력";

    private final WattRepository wattRepository;
    private final TopicAdapter topicAdapter;

    @Override
    public TagPowerMetricResponse get10MinuteWattData(String type, String unit, String per, String tags) {

        String[] topics = getTopics(tags);

        Instant now = Instant.now();
        Instant rawStart = TimeUtil.getRecentMinute(now, 10);
        Instant aggregationStart = TimeUtil.getRecentMinute(now, 10).minus(2, ChronoUnit.HOURS);


        List<Watt> rawWattData = wattRepository.getRawWattData(rawStart, topics, "mqtt_consumer");
        List<Watt> aggregateWattData = wattRepository.getAggregateWattData(aggregationStart, topics, "w_10m");

        List<String> tagList = Arrays.stream(tags.split(",")).collect(Collectors.toList());

        rawWattData.addAll(aggregateWattData);

        Map<Instant, Double> sumByTimezone = rawWattData.stream()
                .collect(Collectors.groupingBy(Watt::getTime,
                        Collectors.summingDouble(Watt::getValue)));

        List<PowerMetric> powerMetrics = sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> PowerMetric.builder()
                        .type(type)
                        .unit(unit)
                        .per(per)
                        .time(entry.getKey())
                        .value(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    @Override
    public TagPowerMetricResponse get1HourWattData(String type, String unit, String per, String tags) {

        String[] topics = getTopics(tags);

        Instant now = Instant.now();
        Instant rawStart = TimeUtil.getRecentHour(now, 1);
        Instant aggregationStart = TimeUtil.getRecentHour(now, 1).minus(23, ChronoUnit.HOURS);

        List<Watt> rawWattData = wattRepository.getRawWattData(rawStart, topics, "mqtt_consumer");
        List<Watt> aggregateWattData = wattRepository.getAggregateWattData(aggregationStart, topics, "w_hour");

        List<String> tagList = Arrays.stream(tags.split(",")).collect(Collectors.toList());

        rawWattData.addAll(aggregateWattData);

        Map<Instant, Double> sumByTimezone = rawWattData.stream()
                .collect(Collectors.groupingBy(Watt::getTime,
                        Collectors.summingDouble(Watt::getValue)));

        List<PowerMetric> powerMetrics = sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> PowerMetric.builder()
                        .type(type)
                        .unit(unit)
                        .per(per)
                        .time(entry.getKey())
                        .value(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    private String[] getTopics(String tags) {
        return topicAdapter.getTopicWithTopics(tags, TOPIC_TYPE_NAME)
                .getTopics().toArray(new String[0]);
    }
}
