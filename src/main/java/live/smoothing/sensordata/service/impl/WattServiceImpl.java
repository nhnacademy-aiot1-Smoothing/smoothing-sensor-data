package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.ThreadLocalUserId;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import live.smoothing.sensordata.service.WattService;
import live.smoothing.sensordata.util.UTCTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static live.smoothing.sensordata.util.PowerMetricUtils.*;

/**
 * WattService 구현체
 *
 * @author  박영준
 */
@Service
@RequiredArgsConstructor
public class WattServiceImpl implements WattService {

    private static final String TOPIC_TYPE_NAME = "전력";
    private static final String RAW_BUCKET_NAME = "powermetrics_data";
    private static final String AGGREGATION_BUCKET_NAME = "aggregation";

    private static final String RAW_MEASUREMENT = "mqtt_consumer";
    private static final String WATT_MIN_MEASUREMENT = "w_10m";
    private static final String WATT_HOUR_MEASUREMENT = "w_hour";


    private final SeriesRepository seriesRepository;
    private final TopicAdapter topicAdapter;

    /**
     * 10분 단위 전력량 데이터를 조회한다.
     *
     * @param tags 사용자 태그
     * @return 전력 데이터
     */
    @Override
    public TagPowerMetricResponse get10MinuteWattData(String tags) {

        String[] topics = getTopics(tags);
        List<String> tagList = getTagList(tags);

        Instant now = Instant.now();
        Instant startTime = UTCTimeUtil.getRecentMinute(now, 10)
                .minus(2, ChronoUnit.HOURS)
                .plus(10, ChronoUnit.MINUTES);
        Instant endTime = UTCTimeUtil.getRecentMinute(now, 10);

        List<Point> aggregateWattData = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                WATT_MIN_MEASUREMENT,
                startTime,
                now.plus(5, ChronoUnit.MINUTES),
                topics
        );

        List<Point> rawWattData = seriesRepository.getSumDataFromStart(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                endTime,
                topics
        );

        for (Point raw : rawWattData) {
            raw.setTime(UTCTimeUtil.getRecentMinute(now, 10));
        }

        aggregateWattData.addAll(rawWattData);

        List<Map.Entry<Instant, Double>> collect = processTimeSeriesData(aggregateWattData, startTime, endTime, ChronoUnit.MINUTES, 10);
        List<PowerMetric> powerMetrics = getWattPowerMetricsByList(collect, TOPIC_TYPE_NAME, "min", "10");

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    /**
     * 1시간 단위 전력 데이터를 조회한다.
     *
     * @param tags 사용자 태그
     * @return 전력 데이터
     */
    @Override
    public TagPowerMetricResponse get1HourWattData(String tags) {

        String[] topics = getTopics(tags);
        List<String> tagList = getTagList(tags);

        Instant now = Instant.now();
        Instant startTime = UTCTimeUtil.getRecentHour(now)
                .minus(1, ChronoUnit.DAYS);
        Instant endTime = UTCTimeUtil.getRecentHour(now);

        List<Point> aggregateWattData = seriesRepository.getDataByPeriod(
                AGGREGATION_BUCKET_NAME,
                WATT_HOUR_MEASUREMENT,
                startTime,
                now.plus(30, ChronoUnit.MINUTES),
                topics
        );

        List<Point> rawWattData = seriesRepository.getSumDataFromStart(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                endTime,
                topics
        );

        for (Point raw : rawWattData) {
            raw.setTime(UTCTimeUtil.getRecentHour(now));
        }

        aggregateWattData.addAll(rawWattData);

        List<Map.Entry<Instant, Double>> collect = processTimeSeriesData(aggregateWattData, startTime, endTime, ChronoUnit.HOURS, 1);
        List<PowerMetric> powerMetrics = getWattPowerMetricsByList(collect, TOPIC_TYPE_NAME, "min", "10");

        return new TagPowerMetricResponse(tagList, powerMetrics);
    }

    /**
     * 태그 문자열 상태에 따라 전체 토픽을 반환하거나 태그에 해당하는 토픽을 반환한다.
     *
     * @param tags 태그 문자열
     * @return 사용자 토픽
     */
    private String[] getTopics(String tags) {

        return tags.isEmpty() ? getTopicAll() : getTagTopics(tags);
    }

    /**
     * 전체 토픽을 반환한다.
     *
     * @return 전체 토픽
     */
    private String[] getTopicAll() {
        return topicAdapter.getTopicAll(TOPIC_TYPE_NAME)
                .getTopics().toArray(new String[0]);
    }

    /**
     * 태그에 해당하는 토픽을 반환한다.
     *
     * @param tags 태그 문자열
     * @return 태그 토픽
     */
    private String[] getTagTopics(String tags) {
        String userId = ThreadLocalUserId.getUserId();

        return topicAdapter.getTopicWithTags(tags, TOPIC_TYPE_NAME, userId)
                .getTopics().toArray(new String[0]);
    }

    /**
     * 태그 문자열을 리스트로 변환한다.
     *
     * @param tags 태그 문자열
     * @return 태그 리스트
     */
    private List<String> getTagList(String tags) {
        return Arrays.stream(tags.split(",")).collect(Collectors.toList());
    }

    /**
     * 시계열 데이터를 처리한다.
     *
     * @param timeSeriesData 시계열 데이터
     * @param start 시작 시간
     * @param end 종료 시간
     * @param intervalUnit 시간 단위
     * @param intervalAmount  시간 간격
     * @return 처리된 시계열 데이터
     */
    private List<Map.Entry<Instant, Double>> processTimeSeriesData(List <Point> timeSeriesData,
                                                                   Instant start,
                                                                   Instant end,
                                                                   ChronoUnit intervalUnit,
                                                                   long intervalAmount) {

        List<Point> deduplicationList = getDeduplicationList(timeSeriesData);
        Map<Instant, Double> sumByTimezone = getSumByTimezone(deduplicationList);
        Map<Instant, Double> fillTimeMap = getFillTimeMap(sumByTimezone,
                start,
                end,
                intervalUnit,
                intervalAmount
        );
        return getSortedByTimeList(fillTimeMap);
    }

    /**
     * PowerMetric 리스트를 반환한다.
     *
     * @param collect 시계열 데이터
     * @param type 타입
     * @param unit 단위
     * @param per 단위 시간
     * @return PowerMetric 리스트
     */
    private static List<PowerMetric> getWattPowerMetricsByList(List<Map.Entry<Instant, Double>> collect, String type, String unit, String per) {
        List<PowerMetric> powerMetrics = new ArrayList<>();

        for (Map.Entry<Instant, Double> entry : collect) {
            powerMetrics.add(
                    new PowerMetric(
                            type,
                            unit,
                            per,
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return powerMetrics;
    }
}
