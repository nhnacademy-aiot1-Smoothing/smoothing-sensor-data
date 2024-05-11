package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.dto.phase.ThreePhase;
import live.smoothing.sensordata.service.ThreePhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor/three-phase")
public class ThreePhaseController {

    private final ThreePhaseService threePhaseService;

    @GetMapping
    public PhaseResponse getThreePhaseData() {
        return threePhaseService.getThreePhase();
    }
}
