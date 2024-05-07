package live.smoothing.sensordata.controller;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.SensorPowerMetricResponse;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.service.KwhService;
import live.smoothing.sensordata.util.TimeUtil;
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
import java.util.List;

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
}