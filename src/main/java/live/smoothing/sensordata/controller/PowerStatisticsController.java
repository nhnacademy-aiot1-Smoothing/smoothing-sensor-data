package live.smoothing.sensordata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PowerStatisticsController {

    @GetMapping("/api/sensor/electrical/range")
    public String getElectricalRange(@RequestParam String type,
                                     @RequestParam Integer unit) {

        return null;
    }

}
