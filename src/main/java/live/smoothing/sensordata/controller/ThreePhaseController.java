package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.service.ThreePhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
