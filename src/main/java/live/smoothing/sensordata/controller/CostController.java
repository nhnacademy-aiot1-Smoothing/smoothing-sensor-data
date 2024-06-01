package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.cost.CostResponse;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.service.GoalService;
import live.smoothing.sensordata.service.KwhService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensor/cost")
@RequiredArgsConstructor
public class CostController {

    private final KwhService kwhService;
    private final GoalService goalService;

    @GetMapping
    public CostResponse getCost() {
        GoalResponse goalResponse = goalService.getGoal();
        Double currentMonthKwh = kwhService.getCurrentMonthKwh();

        return new CostResponse((int)Math.round(goalResponse.getUnitPrice() * currentMonthKwh));
    }
}
