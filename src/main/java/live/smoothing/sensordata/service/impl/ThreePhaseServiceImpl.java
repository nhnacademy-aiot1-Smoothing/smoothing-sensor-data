package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.phase.Phase;
import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.dto.phase.ThreePhase;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import live.smoothing.sensordata.service.ThreePhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreePhaseServiceImpl implements ThreePhaseService {

    private static final String CLASS_LL = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ll_average";
    private static final String CLASS_LN = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ln_average";
    private static final String OFFICE_LL = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ll_average";
    private static final String OFFICE_LN = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ln_average";

    private static final String RAW_BUCKET_NAME = "powermetrics_data";
    private static final String RAW_MEASUREMENT = "mqtt_consumer";

    private final SeriesRepository seriesRepository;

    @Override
    public PhaseResponse getThreePhase() {
        List<Point> voltageData = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                Instant.now().minus(30, ChronoUnit.MINUTES),
                new String[]{CLASS_LL, CLASS_LN, OFFICE_LL, OFFICE_LN}
        );

        Phase classLL =  getPhase(CLASS_LL, voltageData);
        Phase classLN =  getPhase(CLASS_LN, voltageData);
        ThreePhase classA = new ThreePhase(classLL, classLN);

        Phase officeLL = getPhase(OFFICE_LL, voltageData);
        Phase officeLN = getPhase(OFFICE_LN, voltageData);
        ThreePhase office = new ThreePhase(officeLL, officeLN);

        return new PhaseResponse(classA, office);
    }

    private Phase getPhase(String topic, List<Point> points) {
        return points.stream().filter(point -> point.getTopic().equals(topic))
                .map(point -> new Phase(point.getTime(), point.getValue()))
                .findAny()
                .orElse(new Phase(Instant.now(), -1.0));
    }
}
