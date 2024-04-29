package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.dto.PowerMetric;

import java.time.temporal.ChronoUnit;
import java.util.List;

public interface KwhRepository {

    List<Kwh> get24HourData(Long start, ChronoUnit chronoUnit);
    List<Kwh> getWeekData(Long start, ChronoUnit chronoUnit);
}
