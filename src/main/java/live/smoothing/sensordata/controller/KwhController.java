package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.PowerMetricResponse;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.service.KwhService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KwhController {

    private final KwhRepository kwhRepository;

    @GetMapping("api/sensor/usage")
    public PowerMetricResponse getKwh(@RequestParam String type,
                                      @RequestParam String unit,
                                      @RequestParam Integer per,
                                      @RequestParam String tags) {
            return null;
        }

}
