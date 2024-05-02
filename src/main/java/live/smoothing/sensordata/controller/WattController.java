package live.smoothing.sensordata.controller;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.service.WattService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WattController {

    private final WattService wattService;

    @GetMapping("/api/sensor/watt")
    public TagPowerMetricResponse getWattData10Minute (@RequestParam String type,
                                                       @RequestParam String unit,
                                                       @RequestParam String per,
                                                       @RequestParam String tags) {

        if (Integer.parseInt(unit) == 10 && per.equals("min")) {
            return wattService.get10MinuteWattData(type, unit, per, tags);
        } else if (Integer.parseInt(unit) == 1 && per.equals("hour")) {
            return wattService.get1HourWattData(type, unit, per, tags);
        }

        throw new CommonException(HttpStatus.NOT_FOUND, "");
    }
}
