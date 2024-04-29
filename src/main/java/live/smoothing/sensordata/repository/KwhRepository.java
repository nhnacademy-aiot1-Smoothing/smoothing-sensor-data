package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.dto.PowerMetric;

import java.util.List;

public interface KwhRepository {

    List<Kwh> get24HourData();
    List<Kwh> getWeekData();
}
