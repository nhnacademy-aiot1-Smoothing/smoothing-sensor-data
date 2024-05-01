package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.entity.Goal;
import live.smoothing.sensordata.repository.GoalRepository;
import live.smoothing.sensordata.util.TimeProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private GoalServiceImpl goalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(timeProvider.now()).thenReturn(LocalDateTime.of(LocalDate.of(2021, 4, 1), LocalDateTime.MIN.toLocalTime()));
    }

    @Test
    @DisplayName("가장 최근 목표를 가지고 올 때 적절한 횟수로 함수가 호출되고 필드가 일치한다.")
    void getGoal() {
        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000L)
                .unitPrice(300)
                .build();

        when(goalRepository.findFirstByOrderByGoalDateDesc()).thenReturn(goal);

        // when
        GoalResponse response = goalService.getGoal();

        // then
        verify(goalRepository, times(1)).findFirstByOrderByGoalDateDesc();

        Assertions.assertAll(
                () -> assertThat(response.getGoalAmount()).isEqualTo(goal.getGoalAmount()),
                () -> assertThat(response.getUnitPrice()).isEqualTo(goal.getUnitPrice())
        );
    }

    @Test
    @DisplayName("목표 이력을 가지고 올 때 적절한 횟수로 함수가 호출된다.")
    void getGoalHistory() {
        List<Goal> goalList = List.of(
                Goal.builder()
                        .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                        .goalAmount(3000L)
                        .unitPrice(300)
                        .build(),
                Goal.builder()
                        .goalDate(LocalDateTime.of(LocalDate.of(2021, 2, 1), LocalDateTime.MIN.toLocalTime()))
                        .goalAmount(2000L)
                        .unitPrice(200)
                        .build(),
                Goal.builder()
                        .goalDate(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalDateTime.MIN.toLocalTime()))
                        .goalAmount(1000L)
                        .unitPrice(100)
                        .build()
        );

        when(goalRepository.findAllByYear(2021)).thenReturn(goalList);

        // when
        List<GoalHistoryResponse> goalHistory = goalService.getGoalHistory(2021);

        // then
        verify(goalRepository, times(1)).findAllByYear(2021);

        assertThat(goalHistory.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("해당 월에 목표가 존재하지 않으면 false를 반환한다.")
    void notExistGoalReturnFalse() {
        // given
        when(goalRepository.findFirstByOrderByGoalDateDesc()).thenReturn(null);

        // when
        boolean result = goalService.existsByGoalDate();

        // then
        verify(goalRepository, times(1)).findFirstByOrderByGoalDateDesc();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("목표가 존재 하지만, 년도가 다를 경우 false를 반환한다.")
    void findGoalHasDifferentYearReturnFalse() {
        // given
        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2020, 4, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000L)
                .unitPrice(300)
                .build();

        when(goalRepository.findFirstByOrderByGoalDateDesc()).thenReturn(goal);

        // when
        boolean result = goalService.existsByGoalDate();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("목표가 존재 하지만, 월이 다를 경우 false를 반환한다.")
    void findGoalHasDifferentMonthReturnFalse() {
        // given
        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000L)
                .unitPrice(300)
                .build();

        when(goalRepository.findFirstByOrderByGoalDateDesc()).thenReturn(goal);

        // when
        boolean result = goalService.existsByGoalDate();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("년도와 월이 맞는 목표가 존재할 경우 true를 반환한다.")
    void existGoalSuccess() {
        // given
        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 4, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000L)
                .unitPrice(300)
                .build();

        when(goalRepository.findFirstByOrderByGoalDateDesc()).thenReturn(goal);

        // when
        boolean result = goalService.existsByGoalDate();

        // then
        assertThat(result).isTrue();
    }
}