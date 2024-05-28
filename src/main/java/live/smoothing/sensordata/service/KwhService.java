package live.smoothing.sensordata.service;


import live.smoothing.sensordata.dto.SensorPowerMetricResponse;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValueResponse;

import java.time.Instant;

/**
 * 전력 관련 서비스
 *
 * @author 신민석
 */
public interface KwhService {

    TagPowerMetricResponse get48HourData(String tags);

    TagPowerMetricResponse get2WeekData(String tags);

    Double getCurrentMonthKwh();

    TimeZoneResponse getWeeklyDataByTimeOfDay();

    TagPowerMetricResponse getDailyTotalDataByPeriod(Instant start, Instant end, String tags);

    SensorPowerMetricResponse getDailySensorDataByPeriod(Instant start, Instant end, String tags);

    TagSensorValueResponse getTotalSensorData(String tags, Instant start, Instant end);

    TagPowerMetricResponse getHourlyTotalData();
}