package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;

import java.util.List;

public interface GoalService {

    GoalResponse getGoal();

    List<GoalHistoryResponse> getGoalHistory(Integer year);

    void saveGoal(GoalRequest goalRequest);
    void modifyGoal(GoalRequest goalRequest);

    boolean existsByGoalDate();
}
