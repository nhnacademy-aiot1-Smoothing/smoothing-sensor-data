package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import live.smoothing.sensordata.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/sensor/external/usage")
public class UsageController {

    private final UsageService usageService;

    @Autowired
    public UsageController(UsageService usageService) {

        this.usageService = usageService;
    }

    @GetMapping
    public ResponseEntity<EnergyUsageResponse> getEnergyUsage(
            @RequestParam int year,
            @RequestParam String month,
            @RequestParam String bizCd) {

        Optional<EnergyUsageResponse> response = Optional.ofNullable(usageService.getEnergyUsage(year, month, bizCd));
        return ResponseEntity.of(response);
    }
}
