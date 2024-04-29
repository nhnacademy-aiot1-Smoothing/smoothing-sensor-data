package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.PowerMetricResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KwhController {


    @GetMapping("api/sensor/usage")
    public PowerMetricResponse getKwh(@RequestParam String type,
                                      @RequestParam String unit,
                                      @RequestParam Integer per,
                                      @RequestParam String tags) {


            return null;
        }

}
