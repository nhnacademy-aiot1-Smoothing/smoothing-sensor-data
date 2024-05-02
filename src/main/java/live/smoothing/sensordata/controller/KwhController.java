package live.smoothing.sensordata.controller;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.service.KwhService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor/kwh/usage")
public class KwhController {

    private final KwhService kwhService;

    @GetMapping
    public TagPowerMetricResponse getKwh(@RequestParam String type,
                                         @RequestParam String unit,
                                         @RequestParam Integer per,
                                         @RequestParam String tags) {

        if(unit.equals("hour") && per.equals(1)) {
            return kwhService.get24HourData(type, unit, per.toString(), tags);
        } else if(unit.equals("day") && per.equals(7)) {
            return kwhService.getWeekData(type, unit, per.toString(), tags);
        }

        throw new CommonException(HttpStatus.NOT_FOUND, "Not Found");
    }

    @GetMapping("/weekly/by-time-of-day")
    public TimeZoneResponse getWeeklyDataByTimeOfDay() {
        List<KwhTimeZoneResponse> weeklyDataByTimeOfDay = kwhService.getWeeklyDataByTimeOfDay();
        return new TimeZoneResponse(weeklyDataByTimeOfDay);
    }
}