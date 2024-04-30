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

@Service
@RequiredArgsConstructor
public class KwhServiceImpl implements KwhService {

    private final KwhRepository kwhRepository;

    @Override
    public PowerMetricResponse get24HourData() {

        List<Kwh> kwhList = kwhRepository.get24HourData();
        List<PowerMetric> metricList = new ArrayList<>();

        for(int i = 0; i < kwhList.size(); i++) {
            if(kwhList.get(i+1) != null) {
                double useAge = kwhList.get(i+1).getValue() - kwhList.get(i).getValue();

                PowerMetric powerMetric =
                        new PowerMetric(
                                "kwh",
                                "hour",
                                "1",
                                kwhList.get(i).getTime(),
                                useAge
                                );

                metricList.add(powerMetric);
            }
        }
        List<Kwh> rawList = kwhRepository.get24Raw();
        double lastValue = rawList.getLast().getValue();
        double firstValue = rawList.getFirst().getValue();

        PowerMetric powerMetric =
                new PowerMetric(
                        "kwh",
                        "hour",
                        "1",
                        rawList.getFirst().getTime(),
                        lastValue - firstValue);
        metricList.add(powerMetric);
        return new PowerMetricResponse(List.of("kwh"), metricList);
    }
}
