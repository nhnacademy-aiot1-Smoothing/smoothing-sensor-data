package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.SensorWithTopic;
import live.smoothing.sensordata.dto.topic.TopicResponse;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.service.KwhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 전력관련 서비스 구현체
 *
 * @author 신민석, 박영준
 */
@Service
@RequiredArgsConstructor
public class KwhServiceImpl implements KwhService {

    private static final String TOPIC_TYPE_NAME = "전력량";

    private final KwhRepository kwhRepository;
    private final TopicAdapter topicAdapter;

    /**
     * 24시간 동안의 데이터를 조회하여 반환
     *
     * @param type 조회할 데이터의 타입
     * @param unit 조회할 데이터의 단위
     * @param per 조회할 데이터의 주기
     * @param tags 조회할 데이터의 태그
     * @return PowerMetricResponse
     */
    @Override
    public TagPowerMetricResponse get24HourData(String type, String unit, String per, String tags) {

        String[] topics = getTopics(tags);
        List<Kwh> kwhList = kwhRepository.get24HourData(topics);
        List<PowerMetric> metricList = new ArrayList<>();

        for(int i = 0; i < kwhList.size() - 1; i++) {
            if(kwhList.get(i + 1) != null) {
                PowerMetric powerMetric = new PowerMetric("kwh", "hour", "1", kwhList.get(i).getTime(), getGap(kwhList));
                metricList.add(powerMetric);
            }
        }
        List<Kwh> rawList = kwhRepository.get24Raw(topics);

        PowerMetric powerMetric = new PowerMetric("kwh", "hour", "1", rawList.get(0).getTime(), getGap(rawList));
        metricList.add(powerMetric);
        return new TagPowerMetricResponse(List.of(tags), metricList);
    }

    /**
     * 7일 동안의 데이터를 조회하여 반환
     *
     * @param type 조회할 데이터의 타입
     * @param unit 조회할 데이터의 단위
     * @param per 조회할 데이터의 주기
     * @param tags 조회할 데이터의 태그
     * @return PowerMetricResponse
     */
    @Override
    public TagPowerMetricResponse getWeekData(String type, String unit, String per, String tags) {

        String[] topics = getTopics(tags);
        List<Kwh> list = kwhRepository.getWeekData(topics);
        List<PowerMetric> metricList = new ArrayList<>();

        for(int i=0; i < list.size()-1; i++) {
            if(list.get(i + 1) != null) {
                PowerMetric powerMetric = new PowerMetric("kwh", "day", "1", list.get(i).getTime(), getGap(list));
                metricList.add(powerMetric);
            }
        }
        List<Kwh> rawList = kwhRepository.getWeekRaw(topics);
        PowerMetric powerMetric = new PowerMetric("kwh", "day", "1", rawList.get(0).getTime(), getGap(rawList));
        metricList.add(powerMetric);

        return new TagPowerMetricResponse(List.of(tags), metricList);
    }

    @Override
    public Double getCurrentMonthKwh() {

        double result = 0.0;

        TopicResponse topicAll = topicAdapter.getTopicAll(TOPIC_TYPE_NAME);
        String[] topics = topicAll.getTopics().toArray(new String[0]);

        List<Kwh> startDataList = kwhRepository.getCurrentMonthStartData(topics);
        List<Kwh> endDataList = kwhRepository.getCurrentMonthEndData(topics);

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

        Map<Instant, Double> sumByTimezone = weekDataByHour.stream()
                .collect(Collectors.groupingBy(Kwh::getTime,
                        Collectors.summingDouble(Kwh::getValue)));

        List<Map.Entry<Instant, Double>> collect = sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

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

        List<String> topicList;
        if (tags.isEmpty()) {
            topicList = topicAdapter.getTopicAll(TOPIC_TYPE_NAME).getTopics();
        } else {
            topicList = topicAdapter.getTopicWithTopics(tags, TOPIC_TYPE_NAME).getTopics();
        }
        String[] topics = topicList.toArray(new String[0]);

        List<Kwh> weekDataByPeriod = kwhRepository.getDailyDataByPeriod(topics, start, end);

        List<String> tagList = Arrays.stream(tags.split(",")).collect(Collectors.toList());
        List<PowerMetric> powerMetrics = new LinkedList<>();

        Map<Instant, Double> sumByTimezone = weekDataByPeriod.stream()
                .collect(Collectors.groupingBy(Kwh::getTime,
                        Collectors.summingDouble(Kwh::getValue)));

        List<Map.Entry<Instant, Double>> collect = sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (int i = collect.size()-1; i > 0; i--) {
            powerMetrics.add(
                    0,
                    new PowerMetric(
                            "kwh",
                            "day",
                            "1",
                            collect.get(i-1).getKey(),
                            collect.get(i).getValue() - collect.get(i - 1).getValue()
                    )
            );
        }

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    @Override
    public List<SensorPowerMetric> getDailyDataByPeriod(Instant start, Instant end, String tags) {
        SensorTopicResponse sensorWithTopics = topicAdapter.getSensorWithTopics(tags, TOPIC_TYPE_NAME);
        Map<String, String> topicSensorNameMap = new HashMap<>();
        Map<String, List<Kwh>> sensorNameKwhMap = new HashMap<>();
        String[] topics = sensorWithTopics.getSensorWithTopics().stream()
                .map(SensorWithTopic::getTopic)
                .toArray(String[]::new);

        for (SensorWithTopic sensorWithTopic : sensorWithTopics.getSensorWithTopics()) {
            topicSensorNameMap.put(sensorWithTopic.getTopic(), sensorWithTopic.getSensorName());
            sensorNameKwhMap.put(sensorWithTopic.getSensorName(), new ArrayList<>());
        }

        List<Kwh> weekDataByPeriod = kwhRepository.getDailyDataByPeriod(topics, start, end);
        for (Kwh kwh : weekDataByPeriod)
            sensorNameKwhMap.get(topicSensorNameMap.get(kwh.getTopic())).add(kwh);

        List<SensorPowerMetric> sensorPowerMetrics = new ArrayList<>();

        for (Map.Entry<String, List<Kwh>> entry : sensorNameKwhMap.entrySet()) {
            List<PowerMetric> powerMetrics = new LinkedList<>();
            List<Kwh> kwhList = entry.getValue();

            Map<Instant, Double> sumByTimezone = kwhList.stream()
                    .collect(Collectors.groupingBy(Kwh::getTime,
                            Collectors.summingDouble(Kwh::getValue)));

            List<Map.Entry<Instant, Double>> collect = sumByTimezone.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toList());

            for (int i = collect.size()-1; i > 0; i--) {
                powerMetrics.add(
                        0,
                        new PowerMetric(
                                "kwh",
                                "day",
                                "1",
                                collect.get(i-1).getKey(),
                                collect.get(i).getValue() - collect.get(i - 1).getValue()
                        )
                );
            }

            sensorPowerMetrics.add(new SensorPowerMetric(entry.getKey(), powerMetrics));
        }

        return sensorPowerMetrics;
    }


    /**
     * Kwh 리스트의 처음과 끝의 값을 빼서 차이를 반환
     * @param list 처음과 끝의 차이를 구할 Kwh 리스트
     * @return 처음과 끝의 차이
     */
    private double getGap(List<Kwh> list) {
        return list.get(list.size() - 1).getValue() - list.get(0).getValue();
    }

    private String[] getTopics(String tags) {
        return topicAdapter.getTopicWithTopics(tags, TOPIC_TYPE_NAME).getTopics().toArray(new String[0]);
    }

}