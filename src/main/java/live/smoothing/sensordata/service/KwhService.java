package live.smoothing.sensordata.service;


import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValue;

import java.time.Instant;
import java.util.List;

/**
 * 전력 관련 서비스
 *
 * @author 신민석
 */
public interface KwhService {

    TagPowerMetricResponse get48HourData(String per, String tags);

    TagPowerMetricResponse get2WeekData(String per, String tags);

    Double getCurrentMonthKwh();

    List<KwhTimeZoneResponse> getWeeklyDataByTimeOfDay();

    TagPowerMetricResponse getDailyTotalDataByPeriod(Instant start, Instant end, String tags);

    List<SensorPowerMetric> getDailyDataByPeriod(Instant start, Instant end, String tags);

    List<TagSensorValue> getTotalSesnorData(String tags, Instant start, Instant end);

    TagPowerMetricResponse getHourlyTotalData();
}