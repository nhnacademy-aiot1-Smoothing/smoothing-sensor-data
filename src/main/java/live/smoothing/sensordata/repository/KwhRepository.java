package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Kwh;

import java.time.Instant;
import java.util.List;

/**
 * 사용전력량 Repository
 *
 * @author 신민석
 */
public interface KwhRepository {

    List<Kwh> get48HourData(String[] topics);
    List<Kwh> getAggregation2WeekData(String[] topics);
    List<Kwh> getStartData(String[] topics, Instant start);
    List<Kwh> getEndData(String[] topics, Instant start);
    List<Kwh> getWeekDataByHour(String[] topics);
    List<Kwh> getDailyDataByPeriod(String[] topics, Instant start, Instant end);
    List<Kwh> getHourlyTotalData(String[] topics);
}
