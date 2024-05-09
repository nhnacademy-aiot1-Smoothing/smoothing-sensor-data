package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.ThreadLocalUserId;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValue;
import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.SensorWithTopic;
import live.smoothing.sensordata.dto.topic.TopicResponse;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.service.KwhService;
import live.smoothing.sensordata.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 전력관련 서비스 구현체
 *
 * @author 신민석, 박영준
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KwhServiceImpl implements KwhService {

    private static final String TOPIC_TYPE_NAME = "전력량";

    private final KwhRepository kwhRepository;
    private final TopicAdapter topicAdapter;

    /**
     * 최근 24시간 동안의 데이터를 조회하여 반환
     *
     * @param per 조회할 데이터의 주기
     * @param tags 조회할 데이터의 태그
     * @return PowerMetricResponse
     */
    @Override
    public TagPowerMetricResponse get24HourData(String per, String tags) {
        String[] topics = getTopics(tags);
        List<String> tagList = getTagList(tags);

        List<Kwh> aggList = kwhRepository.get24HourData(topics);
        List<PowerMetric> metricList = createHourlyMetricList(aggList, "hour");

        addLastMetric(metricList, topics, "hour", "kwh");
        return new TagPowerMetricResponse(tagList, metricList);

    }

    /**
     * 7일 동안의 데이터를 조회하여 반환
     *
     * @param per 조회할 데이터의 주기
     * @param tags 조회할 데이터의 태그
     * @return PowerMetricResponse
     */
    @Override
    public TagPowerMetricResponse getWeekData(String per, String tags) {
        String[] topics = getTopics(tags);
        log.error("topics: {}", topics);

        List<String> tagList = getTagList(tags);

        List<Kwh> weekAggList = kwhRepository.getAggregationWeekData(topics);

        List<Kwh> firstRawList = kwhRepository.getWeekFirstRaw(topics);
        List<Kwh> lastRawList = kwhRepository.getWeekLastRaw(topics);

        Double firstSum = sumValues(firstRawList);
        Double lastSum = sumValues(lastRawList);
        log.error("firstSum: {}", firstSum);
        log.error("lastSum: {}", lastSum);

        List<PowerMetric> metricList = createDailyMetricList(weekAggList, "day");

        PowerMetric lastMetric = new PowerMetric(
                "kwh",
                "day",
                "1",
                TimeUtil.getRecentDay(Instant.now()).plus(9, ChronoUnit.HOURS),
                lastSum - firstSum

        );
        metricList.add(lastMetric);

        return new TagPowerMetricResponse(tagList, metricList);
    }

    @Override
    public Double getCurrentMonthKwh() {

        double result = 0.0;

        TopicResponse topicAll = topicAdapter.getTopicAll(TOPIC_TYPE_NAME);
        String[] topics = topicAll.getTopics().toArray(new String[0]);

        List<Kwh> startDataList = kwhRepository.getStartData(topics, TimeUtil.getRecentMonth(Instant.now()));
        List<Kwh> endDataList = kwhRepository.getEndData(topics, TimeUtil.getRecentHour(Instant.now()).minus(1, ChronoUnit.HOURS));

        for (Kwh kwh : endDataList) result += kwh.getValue();
        for (Kwh kwh : startDataList) result -= kwh.getValue();

        return result;
    }

    @Override
    public List<KwhTimeZoneResponse> getWeeklyDataByTimeOfDay() {
        List<KwhTimeZoneResponse> kwhTimeZoneResponses = List.of(
                new KwhTimeZoneResponse("evening", 0.0),
                new KwhTimeZoneResponse("afternoon", 0.0),
                new KwhTimeZoneResponse("morning", 0.0),
                new KwhTimeZoneResponse("dawn", 0.0)
        );

        TopicResponse topicAll = topicAdapter.getTopicAll(TOPIC_TYPE_NAME);
        String[] topics = topicAll.getTopics().toArray(new String[0]);

        List<Kwh> weekDataByHour = kwhRepository.getWeekDataByHour(topics);

        Map<Instant, Double> sumByTimezone = getSumByTimezone(weekDataByHour);
        List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);

        for (int i = collect.size()-1, valueIndex = 0; i > 0; i -= 6, valueIndex = (valueIndex + 1) % 4) {
            for (int j = 0; j <= 5; j++) {
                kwhTimeZoneResponses.get(valueIndex)
                                    .setValue(
                                        kwhTimeZoneResponses.get(valueIndex).getValue()
                                        + (collect.get(i - j).getValue() - collect.get(i - j - 1).getValue())
                                    );
            }
        }

        return kwhTimeZoneResponses;
    }

    @Override
    public TagPowerMetricResponse getDailyTotalDataByPeriod(Instant start, Instant end, String tags) {

        String[] topics = getTopics(tags);
        List<Kwh> weekDataByPeriod = kwhRepository.getDailyDataByPeriod(topics, start, end);
        List<String> tagList = Arrays.stream(tags.split(",")).collect(Collectors.toList());

        Map<Instant, Double> sumByTimezone = getSumByTimezone(weekDataByPeriod);
        List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);
        List<PowerMetric> powerMetrics = getPowerMetricsByList(collect, "day");
        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    @Override
    public List<SensorPowerMetric> getDailyDataByPeriod(Instant start, Instant end, String tags) {
        String userId = ThreadLocalUserId.getUserId();
        SensorTopicResponse sensorTopicResponse;
        if (tags.isEmpty()) {
            sensorTopicResponse = topicAdapter.getSensorWithTopicAll(TOPIC_TYPE_NAME);
        } else {
            sensorTopicResponse = topicAdapter.getSensorWithTopics(tags, TOPIC_TYPE_NAME, userId);
        }

        String[] topics = sensorTopicResponse.getSensorWithTopics().stream()
                .map(SensorWithTopic::getTopic)
                .toArray(String[]::new);

        Map<String, String> topicSensorNameMap = new HashMap<>();
        Map<String, List<Kwh>> sensorNameKwhMap = new HashMap<>();

        for (SensorWithTopic sensorWithTopic : sensorTopicResponse.getSensorWithTopics()) {
            topicSensorNameMap.put(sensorWithTopic.getTopic(), sensorWithTopic.getSensorName());
            sensorNameKwhMap.put(sensorWithTopic.getSensorName(), new ArrayList<>());
        }

        List<Kwh> weekDataByPeriod = kwhRepository.getDailyDataByPeriod(topics, start, end);
        for (Kwh kwh : weekDataByPeriod)
            sensorNameKwhMap.get(topicSensorNameMap.get(kwh.getTopic())).add(kwh);

        List<SensorPowerMetric> sensorPowerMetrics = new ArrayList<>();

        for (Map.Entry<String, List<Kwh>> entry : sensorNameKwhMap.entrySet()) {
            List<Kwh> kwhList = entry.getValue();

            Map<Instant, Double> sumByTimezone = getSumByTimezone(kwhList);
            List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);
            List<PowerMetric> powerMetrics = getPowerMetricsByList(collect, "day");
            sensorPowerMetrics.add(new SensorPowerMetric(entry.getKey(), powerMetrics));
        }

        return sensorPowerMetrics;
    }

    @Override
    public List<TagSensorValue> getTotalSesnorData(String tags, Instant start, Instant end) {
        String userId = ThreadLocalUserId.getUserId();
        SensorTopicResponse sensorTopicResponse;
        if (tags.isEmpty()) {
            sensorTopicResponse = topicAdapter.getSensorWithTopicAll(TOPIC_TYPE_NAME);
        } else {
            sensorTopicResponse = topicAdapter.getSensorWithTopics(tags, TOPIC_TYPE_NAME, userId);
        }

        String[] topics = sensorTopicResponse.getSensorWithTopics().stream()
                .map(SensorWithTopic::getTopic)
                .toArray(String[]::new);

        Map<String, String> topicSensorNameMap = new HashMap<>();
        Map<String, Double> sensorValueMap = new HashMap<>();

        for (SensorWithTopic sensorWithTopic : sensorTopicResponse.getSensorWithTopics()) {
            topicSensorNameMap.put(sensorWithTopic.getTopic(), sensorWithTopic.getSensorName());
            sensorValueMap.put(sensorWithTopic.getSensorName(), 0.0);
        }

        List<Kwh> startData = kwhRepository.getStartData(topics, start);
        List<Kwh> endData = kwhRepository.getEndData(topics, Instant.now().minus(1, ChronoUnit.HOURS));

        for (Kwh kwh : endData) {
            sensorValueMap.put(topicSensorNameMap.get(kwh.getTopic()),
                    sensorValueMap.get(topicSensorNameMap.get(kwh.getTopic())) + kwh.getValue());
        }

        for (Kwh kwh : startData) {
            sensorValueMap.put(topicSensorNameMap.get(kwh.getTopic()),
                    sensorValueMap.get(topicSensorNameMap.get(kwh.getTopic())) - kwh.getValue());
        }

        return sensorValueMap.entrySet().stream()
                .map(entry -> new TagSensorValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public TagPowerMetricResponse getHourlyTotalData() {
        String[] topics = getTopicAll();
        List<Kwh> hourlyTotalData = kwhRepository.getHourlyTotalData(topics);

        Map<Instant, Double> sumByTimezone = getSumByTimezone(hourlyTotalData);
        List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);
        List<PowerMetric> powerMetrics = getPowerMetricsByList(collect, "hour");

        return new TagPowerMetricResponse(List.of(), powerMetrics);
    }

    private List<String> getTagList(String tags) {

        return Arrays.stream(tags.split(",")).collect(Collectors.toList());
    }

    private String[] getTopics(String tags) {

        if (tags.isEmpty()) {
            return getTopicAll();
        } else {
            return getTopicWithTags(tags);
        }
    }

    private String[] getTopicAll() {

        return topicAdapter.getTopicAll(TOPIC_TYPE_NAME)
                .getTopics().toArray(new String[0]);
    }

    private String[] getTopicWithTags(String tags) {

        String userId = ThreadLocalUserId.getUserId();

        return topicAdapter.getTopicWithTags(tags, TOPIC_TYPE_NAME, userId)
                .getTopics().toArray(new String[0]);
    }

    private Map<Instant, Double> getSumByTimezone(List<Kwh> kwhList) {
        return kwhList.stream()
                .collect(Collectors.groupingBy(Kwh::getTime,
                        Collectors.summingDouble(Kwh::getValue)));
    }

    private List<Map.Entry<Instant, Double>> getSortedByTimeList(Map<Instant, Double> sumByTimezone) {
        return sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    private List<PowerMetric> getPowerMetricsByList(List<Map.Entry<Instant, Double>> collect, String unit) {
       List<PowerMetric> powerMetrics = new ArrayList<>();

        for (int i = collect.size()-1; i > 0; i--) {
            powerMetrics.add(
                    0,
                    new PowerMetric(
                            "kwh",
                            unit,
                            "1",
                            collect.get(i-1).getKey(),
                            collect.get(i).getValue() - collect.get(i - 1).getValue()
                    )
            );
        }

        return powerMetrics;
    }

    /**
     * 시간별 메트릭 리스트 생성
     */
    private List<PowerMetric> createHourlyMetricList(List<Kwh> aggregationList, String interval) {
        return createMetricList(aggregationList, interval);
    }

    /**
     * 일별 메트릭 리스트 생성
     */
    private List<PowerMetric> createDailyMetricList(List<Kwh> aggregationList, String interval) {
        return createMetricList(aggregationList, interval);
    }

    /**
     * 메트릭 리스트 생성
     */
    private List<PowerMetric> createMetricList(List<Kwh> aggregationList, String interval) {
        List<PowerMetric> metricList = new ArrayList<>();
        List<Map.Entry<Instant, Double>> sortedEntries = getSortedEntriesByTime(aggregationList);

        for (int i = 0; i < sortedEntries.size() - 1; i++) {
            Instant currentKey = sortedEntries.get(i).getKey();
            double diff = sortedEntries.get(i + 1).getValue() - sortedEntries.get(i).getValue();

            PowerMetric powerMetric = new PowerMetric("kwh", interval, "1", currentKey, diff);
            metricList.add(powerMetric);
        }
        return metricList;
    }

    /**
     * 마지막 메트릭 추가
     */
    private void addLastMetric(List<PowerMetric> metricList, String[] topics, String interval, String unit) {
        List<Kwh> firstRaw = interval.equals("day") ? kwhRepository.getWeekFirstRaw(topics) : kwhRepository.get24FirstRaw(topics);
        List<Kwh> lastRaw = interval.equals("day") ? kwhRepository.getWeekLastRaw(topics) : kwhRepository.get24LastRaw(topics);

        double firstValue = firstRaw.stream().mapToDouble(Kwh::getValue).sum();
        double lastValue = lastRaw.stream().mapToDouble(Kwh::getValue).sum();
        double diff = lastValue - firstValue;

        PowerMetric lastMetric = new PowerMetric(unit, interval, "1", TimeUtil.getRecentHour(Instant.now().plus(9, ChronoUnit.HOURS)), diff);
        metricList.add(lastMetric);
    }

    /**
     * 시간별 합계 구하기
     */
    private List<Map.Entry<Instant, Double>> getSortedEntriesByTime(List<Kwh> aggregationList) {
        Map<Instant, Double> sumByTimezone = aggregationList.stream()
                .collect(Collectors.groupingBy(Kwh::getTime, Collectors.summingDouble(Kwh::getValue)));
        return sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    /**
     * 리스트의 합계 계산
     */
    private double sumValues(List<Kwh> kwhList) {
        return kwhList.stream()
                .mapToDouble(Kwh::getValue)
                .sum();
    }

}