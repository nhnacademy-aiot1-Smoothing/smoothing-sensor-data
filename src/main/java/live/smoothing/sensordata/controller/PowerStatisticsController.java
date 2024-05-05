package live.smoothing.sensordata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//Todo: Week, Month, Year 리스트 조회

@RestController
@RequestMapping("/api/sensor/statistics/")
public class PowerStatisticsController {

    @GetMapping
    public String getElectricalRange(@RequestParam String type,
                                     @RequestParam Integer unit) {

        return null;
    }

}
