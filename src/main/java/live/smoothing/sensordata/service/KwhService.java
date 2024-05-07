package live.smoothing.sensordata.service;


import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;

import java.time.Instant;
import java.util.List;

/**
 * 전력 관련 서비스
 *
 * @author 신민석
 */
public interface KwhService {

    TagPowerMetricResponse get24HourData(String type, String unit, String per, String tags);

    TagPowerMetricResponse getWeekData(String type, String unit, String per, String tags);

    Double getCurrentMonthKwh();

    List<KwhTimeZoneResponse> getWeeklyDataByTimeOfDay();

    TagPowerMetricResponse getDailyTotalDataByPeriod(Instant start, Instant end, String tags);

    List<SensorPowerMetric> getDailyDataByPeriod(Instant start, Instant end, String tags);

    TagPowerMetricResponse getHourlyTotalData();
}