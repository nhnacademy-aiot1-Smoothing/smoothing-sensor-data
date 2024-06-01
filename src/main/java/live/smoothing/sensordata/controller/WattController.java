package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.exception.NotFoundServletException;
import live.smoothing.sensordata.service.WattService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor/watt/usage")
public class WattController {

    private final WattService wattService;

    @GetMapping
    public TagPowerMetricResponse getWattData (@RequestParam String unit,
                                               @RequestParam String per,
                                               @RequestParam String tags) {

        if ("min".equals(unit) && Integer.parseInt(per) == 10) {
            return wattService.get10MinuteWattData(tags);
        } else if ("hour".equals(unit) && Integer.parseInt(per) == 1) {
            return wattService.get1HourWattData(tags);
        }

        throw new NotFoundServletException(HttpStatus.NOT_FOUND, "요청을 처리할 수 없습니다.");
    }
}
