package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensor/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public GoalResponse getGoalData() {

        return goalService.getGoal();
    }

    @PostMapping
    public ResponseEntity<Void> saveGoal(@RequestBody GoalRequest goalRequest) {
        if (goalService.existsByGoalDate()) {
            goalService.modifyGoal(goalRequest);
        } else {
            goalService.saveGoal(goalRequest);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/history")
    public List<GoalHistoryResponse> getGoalHistory(@RequestParam(defaultValue = "1") Integer year) {

        return goalService.getGoalHistory(year);
    }
}