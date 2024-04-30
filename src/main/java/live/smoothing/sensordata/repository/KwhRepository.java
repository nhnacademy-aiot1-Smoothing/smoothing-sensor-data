package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.Kwh;

import java.util.List;

public interface KwhRepository {

    List<Kwh> get24HourData(String[] topics);
    List<Kwh> getWeekData(String[] topics);

    List<Kwh> getWeekRaw(String[] topics);
    List<Kwh> get24Raw(String[] topics);
}
