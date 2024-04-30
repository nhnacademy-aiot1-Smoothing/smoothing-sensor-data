package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.Kwh;

import java.util.List;

public interface KwhRepository {

    List<Kwh> get24HourData();
    List<Kwh> getWeekData();

    List<Kwh> getWeekRaw(String measurementName);
    List<Kwh> get24Raw();
}
