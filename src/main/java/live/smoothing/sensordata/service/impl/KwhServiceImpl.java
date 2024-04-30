package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.PowerMetricResponse;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.service.KwhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO: topicAdapter
// TODO: Tag 받아서 파싱.

@Service
@RequiredArgsConstructor
public class KwhServiceImpl implements KwhService {

    private final KwhRepository kwhRepository;
    private final String[] testTopics = {"V"};

    @Override
    public PowerMetricResponse get24HourData(String type, String unit, String per, String tags) {

        List<Kwh> kwhList = kwhRepository.get24HourData(testTopics);
        List<PowerMetric> metricList = new ArrayList<>();

        for(int i = 0; i < kwhList.size() - 1; i++) {
            if(kwhList.get(i + 1) != null) {
                PowerMetric powerMetric = new PowerMetric("kwh", "hour", "1", kwhList.get(i).getTime(), getGap(kwhList));
                metricList.add(powerMetric);
            }
        }
        List<Kwh> rawList = kwhRepository.get24Raw(testTopics);

        PowerMetric powerMetric = new PowerMetric("kwh", "hour", "1", rawList.get(0).getTime(), getGap(rawList));
        metricList.add(powerMetric);
        return new PowerMetricResponse(List.of(tags), metricList);
    }

    @Override
    public PowerMetricResponse getWeekData(String type, String unit, String per, String tags) {

        List<Kwh> list = kwhRepository.getWeekData(testTopics);
        List<PowerMetric> metricList = new ArrayList<>();

        for(int i=0; i < list.size()-1; i++) {
            if(list.get(i + 1) != null) {
                PowerMetric powerMetric = new PowerMetric("kwh", "day", "1", list.get(i).getTime(), getGap(list));
                metricList.add(powerMetric);
            }
        }
        List<Kwh> rawList = kwhRepository.getWeekRaw(testTopics);
        PowerMetric powerMetric = new PowerMetric("kwh", "day", "1", rawList.get(0).getTime(), getGap(rawList));
        metricList.add(powerMetric);

        return new PowerMetricResponse(List.of(tags), metricList);
    }


    private double getGap(List<Kwh> list) {
        return list.get(list.size() - 1).getValue() - list.get(0).getValue();
    }

}