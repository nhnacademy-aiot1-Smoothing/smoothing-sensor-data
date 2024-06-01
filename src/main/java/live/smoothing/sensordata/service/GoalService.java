package live.smoothing.sensordata.service;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;

import java.util.List;

/**
 * 목표 관련 서비스 인터페이스
 *
 * @author 박영준
 */
public interface GoalService {

    /**
     * 이번달 목표를 조회한다.
     *
     * @return 목표 응답
     */
    GoalResponse getGoal();

    /**
     * 특정 연도 목표 이력을 조회한다.
     *
     * @param year 연도
     * @return 목표 이력 응답
     */
    List<GoalHistoryResponse> getGoalHistory(Integer year);

    /**
     * 목표를 수정한다.
     *
     * @param goalRequest 목표 요청
     */
    void modifyGoal(GoalRequest goalRequest);

}
