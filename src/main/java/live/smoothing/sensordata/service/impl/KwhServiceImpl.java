package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.*;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValue;
import live.smoothing.sensordata.dto.kwh.TagSensorValueResponse;
import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.SensorWithTopic;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import live.smoothing.sensordata.service.KwhService;
import live.smoothing.sensordata.util.UTCTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static live.smoothing.sensordata.util.PowerMetricUtils.*;

/**
 * 전력량 서비스 구현체
 *
 * @author 신민석, 박영준
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KwhServiceImpl implements KwhService {

    private static final String RAW_BUCKET_NAME = "powermetrics_data";
    private static final String AGGREGATION_BUCKET_NAME = "aggregation";

    private static final String RAW_MEASUREMENT = "mqtt_consumer";
    private static final String KWH_HOUR_MEASUREMENT = "kwh_hour";
    private static final String KWH_DAILY_MEASUREMENT = "kwh_daily4";

    private static final String TOPIC_TYPE_NAME = "전력량";

    private final SeriesRepository seriesRepository;
    private final TopicAdapter topicAdapter;

    /**
     * 최근 48시간 동안의 전력량을 조회하여 반환
     *
     * @param tags 사용자 태그
     * @return 전력량 데이터
     */
    @Override
    public TagPowerMetricResponse get48HourData(String tags) {

        String[] topics = getTopics(tags);
        List<String> tagList = getTagList(tags);

        Instant start = UTCTimeUtil.getRecentHour(Instant.now())
                .minus(2, ChronoUnit.DAYS)
                .plus(1, ChronoUnit.HOURS);
        Instant end = UTCTimeUtil.getRecentHour(Instant.now());

        List<Point> aggregateData = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                KWH_HOUR_MEASUREMENT,
                start,
                end.plus(30, ChronoUnit.MINUTES),
                topics
        );

        List<Point> rawData = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                Instant.now().minus(20, ChronoUnit.MINUTES),
                topics
        );

        for (Point point : rawData) point.setTime(end.plus(10, ChronoUnit.HOURS));

        aggregateData.addAll(rawData);

        List<Map.Entry<Instant, Double>> collect = processTimeSeriesData(
                aggregateData,
                start,
                end.plus(1, ChronoUnit.HOURS),
                ChronoUnit.HOURS
        );
        List<PowerMetric> metricList = getKwhPowerMetricsByList(collect, TOPIC_TYPE_NAME, "1", "hour");

        return new TagPowerMetricResponse(tagList, metricList);
    }

    /**
     * 최근 14일 동안의 전력량을 조회하여 반환
     *
     * @param tags 사용자 태그
     * @return 전력량 데이터
     */
    @Override
    public TagPowerMetricResponse get2WeekData(String tags) {

        String[] topics = getTopics(tags);
        List<String> tagList = getTagList(tags);

        Instant start = UTCTimeUtil.getRecentDay(Instant.now())
                .minus(13, ChronoUnit.DAYS);
        Instant end = UTCTimeUtil.getRecentDay(Instant.now())
                .plus(1, ChronoUnit.DAYS);

        List<Point> aggregateData = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                KWH_DAILY_MEASUREMENT,
                start,
                end.plus(30, ChronoUnit.MINUTES),
                topics
        );

        List<Point> rawData = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                Instant.now().minus(20, ChronoUnit.MINUTES),
                topics
        );

        for (Point point : rawData) point.setTime(end.plus(9, ChronoUnit.HOURS));

        aggregateData.addAll(rawData);

        List<Map.Entry<Instant, Double>> collect = processTimeSeriesData(
                aggregateData,
                start,
                end,
                ChronoUnit.DAYS
        );

        List<PowerMetric> metricList = getKwhPowerMetricsByList(collect, TOPIC_TYPE_NAME, "1", "day");

        return new TagPowerMetricResponse(tagList, metricList);
    }

    /**
     * 현재 월의 전력량을 조회하여 반환
     *
     * @return 전력량 데이터
     */
    @Override
    public Double getCurrentMonthKwh() {

        double result = 0.0;
        String[] topics = getTopicAll();

        Instant startTime = UTCTimeUtil.getRecentMonth(Instant.now());
        Instant endTime = UTCTimeUtil.getRecentHour(Instant.now())
                .minus(30, ChronoUnit.MINUTES);

        List<Point> startDataList = seriesRepository.getStartData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                startTime,
                topics
        );

        List<Point> endDataList = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                endTime,
                topics
        );

        for (Point startRaw: startDataList) startRaw.setTime(startTime);
        for (Point endRaw: endDataList) endRaw.setTime(endTime);

        List<Point> deduplicationStartList = getDeduplicationList(startDataList);
        List<Point> deduplicationEndList = getDeduplicationList(endDataList);

        for (Point kwh : deduplicationEndList) result += kwh.getValue();
        for (Point kwh : deduplicationStartList) result -= kwh.getValue();

        return result;
    }

    /**
     * 최근 일주일 시간대별 전력량을 조회하여 반환
     * 정각 기준 6시 간격으로 새벽, 아침, 점심, 저녁(밤) 시간대별 전력량을 조회
     *
     * @return 전력량 데이터
     */
    @Override
    public TimeZoneResponse getWeeklyDataByTimeOfDay() {
        String[] topics = getTopicAll();
        List<KwhTimeZoneResponse> kwhTimeZoneResponses = List.of(
                new KwhTimeZoneResponse("evening", 0.0),
                new KwhTimeZoneResponse("afternoon", 0.0),
                new KwhTimeZoneResponse("morning", 0.0),
                new KwhTimeZoneResponse("dawn", 0.0)
        );

        Instant startTime = UTCTimeUtil.getRecentDay(Instant.now())
                .minus(7, ChronoUnit.DAYS);
        Instant lastTime = UTCTimeUtil.getRecentDay(Instant.now());

        List<Point> weekDataByHour = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                KWH_HOUR_MEASUREMENT,
                startTime,
                lastTime.plus(30, ChronoUnit.MINUTES),
                topics
        );

        List<Map.Entry<Instant, Double>> collect = processTimeSeriesData(
                weekDataByHour,
                startTime,
                lastTime,
                ChronoUnit.HOURS
        );

        for (int i = (collect.size()-1), valueIndex = 0; i > 0; i -= 6, valueIndex = (valueIndex + 1) % 4) {
            for (int j = 0; j <= 5; j++) {
                kwhTimeZoneResponses.get(valueIndex)
                                    .setValue(
                                        kwhTimeZoneResponses.get(valueIndex).getValue()
                                        + (collect.get(i - j).getValue() - collect.get(i - j - 1).getValue())
                                    );
            }
        }

        return new TimeZoneResponse(kwhTimeZoneResponses);
    }

    /**
     * 특정 기간 동안의 일별 전력량을 조회하여 반환
     *
     * @param start 시작 시간
     * @param end 종료 시간
     * @param tags 사용자 태그
     * @return 전력량 데이터
     */
    @Override
    public TagPowerMetricResponse getDailyTotalDataByPeriod(Instant start, Instant end, String tags) {

        String[] topics = getTopics(tags);
        List<String> tagList = getTagList(tags);
        List<Point> weekDataByPeriod = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                KWH_DAILY_MEASUREMENT,
                start,
                end.plus(30, ChronoUnit.MINUTES),
                topics
        );

        List<Point> deduplicationList = getDeduplicationList(weekDataByPeriod);
        Map<Instant, Double> sumByTimezone = getSumByTimezone(deduplicationList);
        List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);
        List<PowerMetric> powerMetrics = getKwhPowerMetricsByList(collect, TOPIC_TYPE_NAME, "1", "day");

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    /**
     * 특정 기간 동안의 센서별 일별 전력량을 조회하여 반환
     *
     * @param start 시작 시간
     * @param end 종료 시간
     * @param tags 사용자 태그
     * @return 센서별 전력량 데이터
     */
    @Override
    public SensorPowerMetricResponse getDailySensorDataByPeriod(Instant start, Instant end, String tags) {

        SensorTopicResponse sensorTopicResponse = getTopicWithSensorName(tags);
        String[] topics = sensorTopicResponse.getSensorWithTopics().stream()
                .map(SensorWithTopic::getTopic)
                .toArray(String[]::new);

        Map<String, String> topicSensorNameMap = new HashMap<>();
        Map<String, List<Point>> sensorNameKwhMap = new HashMap<>();

        for (SensorWithTopic sensorWithTopic : sensorTopicResponse.getSensorWithTopics()) {
            topicSensorNameMap.put(sensorWithTopic.getTopic(), sensorWithTopic.getSensorName());
            sensorNameKwhMap.put(sensorWithTopic.getSensorName(), new ArrayList<>());
        }

        List<Point> weekDataByPeriod = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                KWH_DAILY_MEASUREMENT,
                start,
                end.plus(30, ChronoUnit.MINUTES),
                topics
        );

        for (Point kwh : weekDataByPeriod)
            sensorNameKwhMap.get(topicSensorNameMap.get(kwh.getTopic())).add(kwh);

        List<SensorPowerMetric> sensorPowerMetrics = new ArrayList<>();

        for (Map.Entry<String, List<Point>> entry : sensorNameKwhMap.entrySet()) {
            List<Point> kwhList = entry.getValue();

            List<Point> deduplicationList = getDeduplicationList(kwhList);
            Map<Instant, Double> sumByTimezone = getSumByTimezone(deduplicationList);
            List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);
            List<PowerMetric> powerMetrics = getKwhPowerMetricsByList(collect, TOPIC_TYPE_NAME, "1", "day");
            sensorPowerMetrics.add(new SensorPowerMetric(entry.getKey(), powerMetrics));
        }

        return new SensorPowerMetricResponse(sensorPowerMetrics);
    }

    /**
     * 특정 기간 동안의 센서별 전체 전력량을 조회하여 반환
     *
     * @param tags 사용자 태그
     * @param start 시작 시간
     * @param end 종료 시간
     * @return  센서별 전력량 데이터
     */
    @Override
    public TagSensorValueResponse getTotalSensorData(String tags, Instant start, Instant end) {

        SensorTopicResponse sensorTopicResponse = getTopicWithSensorName(tags);
        String[] topics = sensorTopicResponse.getSensorWithTopics().stream()
                .map(SensorWithTopic::getTopic)
                .toArray(String[]::new);

        Map<String, String> topicSensorNameMap = new HashMap<>();
        Map<String, Double> sensorValueMap = new HashMap<>();

        for (SensorWithTopic sensorWithTopic : sensorTopicResponse.getSensorWithTopics()) {
            topicSensorNameMap.put(sensorWithTopic.getTopic(), sensorWithTopic.getSensorName());
            sensorValueMap.put(sensorWithTopic.getSensorName(), 0.0);
        }

        List<Point> startData = seriesRepository.getStartData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                start,
                topics
        );

        List<Point> endData = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                end,
                topics
        );

        for (Point point : endData) {
            sensorValueMap.put(topicSensorNameMap.get(point.getTopic()),
                    sensorValueMap.get(topicSensorNameMap.get(point.getTopic())) + point.getValue());
        }

        for (Point point : startData) {
            sensorValueMap.put(topicSensorNameMap.get(point.getTopic()),
                    sensorValueMap.get(topicSensorNameMap.get(point.getTopic())) - point.getValue());
        }

        List<TagSensorValue> tagSensorValueList = sensorValueMap.entrySet().stream()
                .map(entry -> new TagSensorValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new TagSensorValueResponse(tagSensorValueList);
    }

    /**
     * 현재 집계되어 있는 1시간 단위 전력량을 모두 조회하여 반환
     *
     * @return 전력량 데이터
     */
    @Override
    public TagPowerMetricResponse getHourlyTotalData() {
        String[] topics = getTopicAll();
        List<Point> hourlyTotalData = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                KWH_HOUR_MEASUREMENT,
                Instant.ofEpochMilli(0),
                Instant.now(),
                topics
        );

        List<Point> deduplicationList = getDeduplicationList(hourlyTotalData);
        Map<Instant, Double> sumByTimezone = getSumByTimezone(deduplicationList);
        List<Map.Entry<Instant, Double>> collect = getSortedByTimeList(sumByTimezone);
        List<PowerMetric> powerMetrics = getKwhPowerMetricsByList(collect, TOPIC_TYPE_NAME, "1", "hour");

        return new TagPowerMetricResponse(List.of(), powerMetrics);
    }

    /**
     * 시계열 데이터를 처리한다.
     *
     * @param timeSeriesData 시계열 데이터
     * @param start 시작 시간
     * @param end 종료 시간
     * @param intervalUnit 시간 단위
     * @return 처리된 시계열 데이터
     */
    private List<Map.Entry<Instant, Double>> processTimeSeriesData(List <Point> timeSeriesData,
                                                                   Instant start,
                                                                   Instant end,
                                                                   ChronoUnit intervalUnit) {

        List<Point> deduplicationList = getDeduplicationList(timeSeriesData);
        Map<Instant, Double> sumByTimezone = getSumByTimezone(deduplicationList);
        Map<Instant, Double> fillTimeMap = getFillTimeMap(sumByTimezone,
                start,
                end,
                intervalUnit,
                1
        );
        return getSortedByTimeList(fillTimeMap);
    }

    /**
     * 태그 문자열을 리스트로 변환
     *
     * @param tags 태그 문자열
     * @return 태그 리스트
     */
    private List<String> getTagList(String tags) {

        return Arrays.stream(tags.split(",")).collect(Collectors.toList());
    }

    /**
     * 태그 문자열 상태에 따라 전체 토픽 또는 태그가 있는 토픽을 조회
     *
     * @param tags 태그 문자열
     * @return 토픽 배열
     */
    private String[] getTopics(String tags) {

        return tags.isEmpty() ? getTopicAll() : getTopicWithTags(tags);
    }

    /**
     * 전체 토픽 조회
     *
     * @return 토픽 배열
     */
    private String[] getTopicAll() {

        return topicAdapter.getTopicAll(TOPIC_TYPE_NAME)
                .getTopics().toArray(new String[0]);
    }

    /**
     * 태그에 해당하는 토픽 조회
     *
     * @param tags 태그 문자열
     * @return 토픽 배열
     */
    private String[] getTopicWithTags(String tags) {

        String userId = ThreadLocalUserId.getUserId();

        return topicAdapter.getTopicWithTags(tags, TOPIC_TYPE_NAME, userId)
                .getTopics().toArray(new String[0]);
    }

    /**
     * 태그 문자열 상태에 따라 전체 센서 또는 태그가 있는 센서를 조회
     *
     * @param tags 태그 문자열
     * @return 토픽 센서 응답
     */
    private SensorTopicResponse getTopicWithSensorName(String tags) {
        return tags.isEmpty() ? getTopicWithSensorAll() : getTopicWithSensors(tags);
    }

    /**
     * 태그에 해당하는 센서 토픽 조회
     *
     * @param tags 태그 문자열
     * @return 토픽 센서 응답
     */
    private SensorTopicResponse getTopicWithSensors(String tags) {

            String userId = ThreadLocalUserId.getUserId();
            return topicAdapter.getSensorWithTopics(tags, TOPIC_TYPE_NAME, userId);
    }

    /**
     * 전체 센서 토픽 조회
     *
     * @return 토픽 센서 응답
     */
    private SensorTopicResponse getTopicWithSensorAll() {

            return topicAdapter.getSensorWithTopicAll(TOPIC_TYPE_NAME);
    }

    /**
     * PowerMetric 리스트를 반환한다.
     *
     * @param collect 시계열 데이터
     * @param type 타입
     * @param per 단위
     * @param unit 단위
     * @return PowerMetric 리스트
     */
    private static List<PowerMetric> getKwhPowerMetricsByList(List<Map.Entry<Instant, Double>> collect, String type, String per, String unit) {
        List<PowerMetric> powerMetrics = new ArrayList<>();

        for (int i = collect.size()-1; i > 0; i--) {
            powerMetrics.add(
                    0,
                    new PowerMetric(
                            type,
                            unit,
                            per,
                            collect.get(i-1).getKey(),
                            collect.get(i).getValue() - collect.get(i - 1).getValue()
                    )
            );
        }

        return powerMetrics;
    }
}
