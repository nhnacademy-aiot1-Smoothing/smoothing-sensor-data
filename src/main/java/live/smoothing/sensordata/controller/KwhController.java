package live.smoothing.sensordata.controller;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.watt.PowerMetricResponse;
import live.smoothing.sensordata.service.KwhService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KwhController {

    private final KwhService kwhService;

    @GetMapping("api/sensor/usage")
    public PowerMetricResponse getKwh(@RequestParam String type,
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
}