package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Point;

import java.time.Instant;
import java.util.List;

/**
 * 사용전력량 Repository
 *
 * @author 박영준
 */
public interface SeriesRepository {

    List<Point> getStartData(String bucket, String measurement, Instant start, String[] topics);
    List<Point> getEndData(String bucket, String measurement, Instant start, String[] topics);
    List<Point> getDataByPeriod(String bucket, String measurement,  Instant start, Instant end, String[] topics);
    List<Point> getSumDataFromStart(String bucket, String measurement, Instant start, String[] topics);
}
