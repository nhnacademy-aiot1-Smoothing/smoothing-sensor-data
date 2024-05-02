package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Kwh;

import java.util.List;

/**
 * 사용전력량 Repository
 *
 * @author 신민석
 */
public interface KwhRepository {

    List<Kwh> get24HourData(String[] topics);
    List<Kwh> getWeekData(String[] topics);

    List<Kwh> getWeekRaw(String[] topics);
    List<Kwh> get24Raw(String[] topics);

    List<Kwh> getCurrentMonthStartData(String[] topics);

    List<Kwh> getCurrentMonthEndData(String[] topics);

    List<Kwh> getWeekDataByHour(String[] topics);
}
