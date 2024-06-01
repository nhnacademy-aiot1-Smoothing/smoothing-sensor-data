package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.SensorPowerMetricResponse;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValueResponse;
import live.smoothing.sensordata.exception.NotFoundServletException;
import live.smoothing.sensordata.service.KwhService;
import live.smoothing.sensordata.util.UTCTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor/kwh/usage")
public class KwhController {

    private final KwhService kwhService;

    @GetMapping
    public TagPowerMetricResponse getKwh(@RequestParam String unit,
                                         @RequestParam Integer per,
                                         @RequestParam String tags) {

        if(unit.equals("hour") && per.equals(1)) {
            return kwhService.get48HourData(tags);
        } else if(unit.equals("day") && per.equals(1)) {
            return kwhService.get2WeekData(tags);
        }

        throw new NotFoundServletException(HttpStatus.NOT_FOUND, "요청을 처리할 수 없습니다.");
    }

    @GetMapping("/weekly/timezone")
    public TimeZoneResponse getWeeklyDataByTimeOfDay() {
        return  kwhService.getWeeklyDataByTimeOfDay();
    }

    @GetMapping("/daily/period/total")
    public TagPowerMetricResponse getDailyTotalDataByPeriod(@RequestParam LocalDateTime start,
                                                            @RequestParam LocalDateTime end,
                                                            @RequestParam String tags) {

        Instant startInstant = UTCTimeUtil.getRecentDay(start.toInstant(ZoneOffset.UTC));
        Instant endInstant = UTCTimeUtil.getRecentDay(end.toInstant(ZoneOffset.UTC))
                .plus(1, ChronoUnit.DAYS);

        return kwhService.getDailyTotalDataByPeriod(startInstant, endInstant, tags);
    }

    @GetMapping("/daily/period")
    public SensorPowerMetricResponse getDailyDataByPeriod(@RequestParam LocalDateTime start,
                                                          @RequestParam LocalDateTime end,
                                                          @RequestParam String tags) {

        Instant startInstant = UTCTimeUtil.getRecentDay(start.toInstant(ZoneOffset.UTC));
        Instant endInstant = UTCTimeUtil.getRecentDay(end.toInstant(ZoneOffset.UTC))
                .plus(1, ChronoUnit.DAYS);

        return kwhService.getDailySensorDataByPeriod(startInstant, endInstant, tags);
    }

    @GetMapping("/daily/value/total")
    public TagSensorValueResponse getDailyTotalSensorData(@RequestParam String tags) {
        Instant start = Instant.now().minus(24, ChronoUnit.HOURS);
        Instant end = Instant.now().minus(30, ChronoUnit.MINUTES);

        return kwhService.getTotalSensorData(tags, start, end);
    }

    @GetMapping("/weekly/value/total")
    public TagSensorValueResponse getWeeklyTotalSensorData(@RequestParam String tags) {
        Instant start = Instant.now().minus(7, ChronoUnit.DAYS);
        Instant end = Instant.now().minus(30, ChronoUnit.MINUTES);

        return kwhService.getTotalSensorData(tags, start, end);
    }

    @GetMapping("/hourly/total")
    public TagPowerMetricResponse getHourlyTotalData() {
        return kwhService.getHourlyTotalData();
    }
}