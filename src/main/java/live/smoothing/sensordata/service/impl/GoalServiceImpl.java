package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.enttiy.Goal;
import live.smoothing.sensordata.repository.GoalRepository;
import live.smoothing.sensordata.service.GoalService;
import live.smoothing.sensordata.util.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final TimeProvider timeProvider;

    @Override
    public GoalResponse getGoal() {

        Goal findGoal = goalRepository.findFirstByOrderByGoalDateDesc();
        return new GoalResponse(findGoal.getGoalAmount(), findGoal.getUnitPrice());
    }

    @Override
    public List<GoalHistoryResponse> getGoalHistory(Integer year) {

        goalRepository.findAllByYear(year);

        // TODO: 전력량 조회
        return null;
    }


    @Override
    public void saveGoal(GoalRequest goalRequest) {

        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.now())
                .goalAmount(goalRequest.getGoalAmount())
                .unitPrice(goalRequest.getUnitPrice())
                .build();

        goalRepository.save(goal);
    }

    @Override
    public void modifyGoal(GoalRequest goalRequest) {

        Goal goal = goalRepository.findFirstByOrderByGoalDateDesc();

        goal.setGoalAmount(goalRequest.getGoalAmount());
        goal.setUnitPrice(goalRequest.getUnitPrice());

        goalRepository.save(goal);
    }

    @Override
    public boolean existsByGoalDate() {

        Goal findGoal = goalRepository.findFirstByOrderByGoalDateDesc();

        return Objects.nonNull(findGoal)
                && findGoal.getGoalDate().getYear() == timeProvider.now().getYear()
                && findGoal.getGoalDate().getMonth() == timeProvider.now().getMonth();
    }
}
