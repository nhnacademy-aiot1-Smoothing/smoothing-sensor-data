package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.entity.Goal;
import live.smoothing.sensordata.exception.NotFoundGoalException;
import live.smoothing.sensordata.repository.GoalRepository;
import live.smoothing.sensordata.service.GoalService;
import live.smoothing.sensordata.util.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 목표 서비스 구현체
 *
 * @author 박영준
 */
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private static final String NOT_FOUND_GOAL_MESSAGE = "해당 목표가 존재하지 않습니다.";

    private final GoalRepository goalRepository;
    private final TimeProvider timeProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public GoalResponse getGoal() {
        LocalDateTime now = timeProvider.now();
        Goal findGoal = goalRepository.findByYearAndMonth(now.getYear(), now.getMonthValue())
                .orElseThrow(() -> new NotFoundGoalException(HttpStatus.NOT_FOUND, NOT_FOUND_GOAL_MESSAGE));

        return new GoalResponse(findGoal.getGoalAmount(), findGoal.getUnitPrice());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GoalHistoryResponse> getGoalHistory(Integer year) {

        List<Goal> goals = goalRepository.findAllByYear(year);

        return goals.stream().map(goal ->
                new GoalHistoryResponse(
                        goal.getGoalDate(),
                        goal.getGoalAmount(),
                        goal.getAmount()
                )
        ).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyGoal(GoalRequest goalRequest) {
        LocalDateTime now = timeProvider.now();
        Goal goal = goalRepository.findByYearAndMonth(now.getYear(), now.getMonthValue())
                .orElse(Goal.builder().goalDate(LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1, 0, 0)).build());

        goal.setGoalAmount(goalRequest.getGoalAmount());
        goal.setUnitPrice(goalRequest.getUnitPrice());

        goalRepository.save(goal);
    }

}
