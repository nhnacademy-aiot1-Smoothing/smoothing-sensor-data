package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import live.smoothing.sensordata.service.IgrService;
import live.smoothing.sensordata.util.UTCTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IgrServiceImpl implements IgrService {

    private static final String IGR_CLASS_A = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/main/ph/total/de/igr";
    private static final String IGR_OFFICE_A = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/main/ph/total/de/igr";

    private static final String RAW_BUCKET_NAME = "powermetrics_data";
    private static final String RAW_MEASUREMENT = "mqtt_consumer";

    private final SeriesRepository seriesRepository;

    @Override
    public Igr getClassIgr() {
        List<Point> endData = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                UTCTimeUtil.getRecentMinute(Instant.now(), 10)
                        .minus(10, ChronoUnit.MINUTES),
                new String[]{IGR_CLASS_A}
        );

        return endData.isEmpty() ? new Igr(-1.0) : new Igr(endData.get(0).getValue());
    }

    @Override
    public Igr getOfficeIgr() {
        List<Point> endData = seriesRepository.getEndData(
                RAW_BUCKET_NAME,
                RAW_MEASUREMENT,
                UTCTimeUtil.getRecentMinute(Instant.now(), 10)
                        .minus(10, ChronoUnit.MINUTES),
                new String[]{IGR_OFFICE_A}
        );

        return endData.isEmpty() ? new Igr(-1.0) : new Igr(endData.get(0).getValue());
    }
}
