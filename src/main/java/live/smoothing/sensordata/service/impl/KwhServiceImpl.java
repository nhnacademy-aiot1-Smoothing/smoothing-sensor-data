package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.watt.PowerMetric;
import live.smoothing.sensordata.dto.watt.PowerMetricResponse;
import live.smoothing.sensordata.dto.watt.SensorTopicResponse;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.service.KwhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO: topicAdapter
// TODO: Tag 받아서 파싱.
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
    public PowerMetricResponse get24HourData(String type, String unit, String per, String tags) {

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
        return new PowerMetricResponse(List.of(tags), metricList);
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
    public PowerMetricResponse getWeekData(String type, String unit, String per, String tags) {

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

        return new PowerMetricResponse(List.of(tags), metricList);
    }

    @Override
    public Double getCurrentMonthKwh() {

        double result = 0.0;

        SensorTopicResponse topicAll = topicAdapter.getTopicAll(TOPIC_TYPE_NAME);
        String[] topics = topicAll.getTopics().toArray(new String[0]);

        List<Kwh> startDataList = kwhRepository.getCurrentMonthStartData(topics);
        List<Kwh> endDataList = kwhRepository.getCurrentMonthEndData(topics);

        for (int i = 0; i < startDataList.size(); i++) {
            result += endDataList.get(i).getValue() - startDataList.get(i).getValue();
        }

        return result;
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