package live.smoothing.sensordata.service;


import live.smoothing.sensordata.dto.watt.PowerMetricResponse;

/**
 * 전력 관련 서비스
 *
 * @author 신민석
 */
public interface KwhService {

    PowerMetricResponse get24HourData(String type, String unit, String per, String tags);

    PowerMetricResponse getWeekData(String type, String unit, String per, String tags);

    Double getCurrentMonthKwh();
}