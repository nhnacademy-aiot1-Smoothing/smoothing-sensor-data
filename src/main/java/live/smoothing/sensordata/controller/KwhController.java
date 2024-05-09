package live.smoothing.sensordata.controller;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.SensorPowerMetricResponse;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValueResponse;
import live.smoothing.sensordata.service.KwhService;
import live.smoothing.sensordata.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Influx DB에서 Kwh(전력사용량) 데이터 조회를 위한 Controller
 *
 * @author 신민석
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor/kwh/usage")
public class KwhController {

    private final KwhService kwhService;

    /**
     *
     * @param type 조회할 데이터 유형
     * @param unit 시간 단위(ex. hour)
     * @param per 시간 단위(ex. 1)
     * @param tags 조회할 태그들
     * @return 태그별로 구분된 전력 사용량 데이터를 반환
     */
    @GetMapping
    public TagPowerMetricResponse getKwh(@RequestParam String unit,
                                         @RequestParam Integer per,
                                         @RequestParam String tags) {

        if(unit.equals("hour") && per.equals(1)) {
            return kwhService.get24HourData(per.toString(), tags);
        } else if(unit.equals("day") && per.equals(1)) {
            return kwhService.getWeekData(per.toString(), tags);
        }

        throw new CommonException(HttpStatus.NOT_FOUND, "Not Found");
    }

    @GetMapping("/weekly/timezone")
    public TimeZoneResponse getWeeklyDataByTimeOfDay() {
        List<KwhTimeZoneResponse> weeklyDataByTimeOfDay = kwhService.getWeeklyDataByTimeOfDay();
        return new TimeZoneResponse(weeklyDataByTimeOfDay);
    }

    @GetMapping("/daily/period/total")
    public TagPowerMetricResponse getDailyTotalDataByPeriod(@RequestParam LocalDateTime start,
                                                            @RequestParam LocalDateTime end,
                                                            @RequestParam String tags) {

        Instant startInstant = TimeUtil.getRecentDay(start.toInstant(ZoneOffset.UTC));
        Instant endInstant = TimeUtil.getRecentDay(end.toInstant(ZoneOffset.UTC))
                .plus(1, ChronoUnit.DAYS)
                .plus(30, ChronoUnit.MINUTES);

        return kwhService.getDailyTotalDataByPeriod(startInstant, endInstant, tags);
    }

    @GetMapping("/daily/period")
    public SensorPowerMetricResponse getDailyDataByPeriod(@RequestParam LocalDateTime start,
                                                          @RequestParam LocalDateTime end,
                                                          @RequestParam String tags) {

        Instant startInstant = TimeUtil.getRecentDay(start.toInstant(ZoneOffset.UTC));
        Instant endInstant = TimeUtil.getRecentDay(end.toInstant(ZoneOffset.UTC))
                .plus(1, ChronoUnit.DAYS)
                .plus(30, ChronoUnit.MINUTES);

        List<SensorPowerMetric> dailyDataByPeriod = kwhService.getDailyDataByPeriod(startInstant, endInstant, tags);
        return new SensorPowerMetricResponse(dailyDataByPeriod);
    }

    @GetMapping("/daily/value/total")
    public TagSensorValueResponse getDailyTotalSensorData(@RequestParam String tags) {
        Instant start = Instant.now().minus(24, ChronoUnit.HOURS);
        Instant end = Instant.now().minus(1, ChronoUnit.HOURS);

        return new TagSensorValueResponse(kwhService.getTotalSesnorData(tags, start, end));
    }

    @GetMapping("/weekly/value/total")
    public TagSensorValueResponse getWeeklyTotalSensorData(@RequestParam String tags) {
        Instant start = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant end = Instant.now().minus(1, ChronoUnit.HOURS);

        return new TagSensorValueResponse(kwhService.getTotalSesnorData(tags, start, end));
    }

    @GetMapping("/hourly/total")
    public TagPowerMetricResponse getHourlyTotalData() {
        return kwhService.getHourlyTotalData();
    }
}