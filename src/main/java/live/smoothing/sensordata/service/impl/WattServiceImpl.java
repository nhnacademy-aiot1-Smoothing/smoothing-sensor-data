package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.ThreadLocalUserId;
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
        rawWattData.addAll(aggregateWattData);

        List<String> tagList = getTagList(tags);
        Map<Instant, Double> sumByTimezone = getSumByTimezone(rawWattData);
        List<PowerMetric> powerMetrics = getPowerMetricsByMap(sumByTimezone, type, unit, per);

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    @Override
    public TagPowerMetricResponse get1HourWattData(String type, String unit, String per, String tags) {

        String[] topics = getTopics(tags);

        Instant now = Instant.now();
        Instant rawStart = TimeUtil.getRecentHour(now);
        Instant aggregationStart = TimeUtil.getRecentHour(now).minus(23, ChronoUnit.HOURS);

        List<Watt> rawWattData = wattRepository.getRawWattData(rawStart, topics, "mqtt_consumer");
        List<Watt> aggregateWattData = wattRepository.getAggregateWattData(aggregationStart, topics, "w_hour");
        rawWattData.addAll(aggregateWattData);

        List<String> tagList = getTagList(tags);
        Map<Instant, Double> sumByTimezone = getSumByTimezone(rawWattData);
        List<PowerMetric> powerMetrics = getPowerMetricsByMap(sumByTimezone, type, unit, per);

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    private String[] getTopics(String tags) {
        String userId = ThreadLocalUserId.getUserId();

        if (tags.isEmpty()) {
            return topicAdapter.getTopicAll(TOPIC_TYPE_NAME)
                    .getTopics().toArray(new String[0]);
        } else {
            return topicAdapter.getTopicWithTags(tags, TOPIC_TYPE_NAME, userId)
                    .getTopics().toArray(new String[0]);
        }
    }

    private List<String> getTagList(String tags) {
        return Arrays.stream(tags.split(",")).collect(Collectors.toList());
    }

    private Map<Instant, Double> getSumByTimezone(List<Watt> wattList) {
        return wattList.stream()
                .collect(Collectors.groupingBy(Watt::getTime,
                        Collectors.summingDouble(Watt::getValue)));
    }

    private List<PowerMetric> getPowerMetricsByMap(Map<Instant, Double> sumByTimezone, String type, String unit, String per) {
        return sumByTimezone.entrySet().stream()
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
    }
}
