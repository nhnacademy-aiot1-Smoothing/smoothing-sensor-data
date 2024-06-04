package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.goal.GoalHistoryResponse;
import live.smoothing.sensordata.dto.goal.GoalRequest;
import live.smoothing.sensordata.dto.goal.GoalResponse;
import live.smoothing.sensordata.entity.Goal;
import live.smoothing.sensordata.exception.NotFoundGoalException;
import live.smoothing.sensordata.repository.GoalRepository;
import live.smoothing.sensordata.util.TimeProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(timeProvider.now()).thenReturn(LocalDateTime.of(2021, 3, 1, 0, 0));
    }

    @Test
    @DisplayName("가장 최근 목표를 가지고 올 때 적절한 횟수로 함수가 호출되고 필드가 일치한다.")
    void getGoal() {
        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000.0)
                .unitPrice(300)
                .build();

        when(goalRepository.findByYearAndMonth(anyInt(), anyInt())).thenReturn(Optional.of(goal));

        // when
        GoalResponse response = goalService.getGoal();

        // then
        verify(goalRepository, times(1)).findByYearAndMonth(anyInt(), anyInt());

        Assertions.assertAll(
                () -> assertThat(response.getGoalAmount()).isEqualTo(goal.getGoalAmount()),
                () -> assertThat(response.getUnitPrice()).isEqualTo(goal.getUnitPrice())
        );
    }

    @Test
    @DisplayName("가장 최근 목표를 가지고 올 때 해당 목표가 존재하지 않는다면 NotFoundGoalException 발생.")
    void getGoal_throws_NotFoundGoalException() {
        // when
        when(goalRepository.findByYearAndMonth(anyInt(), anyInt())).thenReturn(Optional.empty());

        // when
        NotFoundGoalException exception = assertThrows(NotFoundGoalException.class, () -> goalService.getGoal());

        // then
        verify(goalRepository, times(1)).findByYearAndMonth(anyInt(), anyInt());
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("목표 이력을 가지고 올 때 적절한 횟수로 함수가 호출된다.")
    void getGoalHistory() {
        List<Goal> goalList = List.of(
                Goal.builder()
                        .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                        .goalAmount(3000.0)
                        .unitPrice(300)
                        .build(),
                Goal.builder()
                        .goalDate(LocalDateTime.of(LocalDate.of(2021, 2, 1), LocalDateTime.MIN.toLocalTime()))
                        .goalAmount(2000.0)
                        .unitPrice(200)
                        .build(),
                Goal.builder()
                        .goalDate(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalDateTime.MIN.toLocalTime()))
                        .goalAmount(1000.0)
                        .unitPrice(100)
                        .build()
        );

        when(goalRepository.findAllByYear(2021)).thenReturn(goalList);

        // when
        List<GoalHistoryResponse> goalHistory = goalService.getGoalHistory(2021);

        // then
        verify(goalRepository, times(1)).findAllByYear(2021);
        assertThat(goalHistory).hasSize(3);
    }

    @Test
    @DisplayName("목표를 수정할 때 적절한 횟수로 함수가 호출된다.")
    void modifyGoal() {
        // given
        Goal goal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000.0)
                .unitPrice(300)
                .build();

        when(goalRepository.findByYearAndMonth(anyInt(), anyInt())).thenReturn(Optional.of(goal));

        GoalRequest goalRequest = new GoalRequest();
        ReflectionTestUtils.setField(goalRequest, "goalAmount", 2000.0);
        ReflectionTestUtils.setField(goalRequest, "unitPrice", 200);

        // when
        goalService.modifyGoal(goalRequest);

        // then
        verify(goalRepository, times(1)).findByYearAndMonth(anyInt(), anyInt());
        verify(goalRepository, times(1)).save(any());

        assertThat(goal.getGoalAmount()).isEqualTo(2000.0);
        assertThat(goal.getUnitPrice()).isEqualTo(200);
    }

    //not found 없애서 원래 테스트 주석처리 했슴다
//    @Test
//    @DisplayName("목표를 수정할 때 해당 목표가 존재하지 않는다면 NotFoundGoalException 발생")
//    void modifyGoal_throws_NotFoundGoalException() {
//        // when
//        when(goalRepository.findByYearAndMonth(anyInt(), anyInt())).thenReturn(Optional.empty());
//        GoalRequest goalRequest = new GoalRequest();
//        ReflectionTestUtils.setField(goalRequest, "goalAmount", 0.0);
//        ReflectionTestUtils.setField(goalRequest, "unitPrice", 0);
//
//        // when
//        NotFoundGoalException exception = assertThrows(NotFoundGoalException.class, () -> goalService.modifyGoal(goalRequest));
//
//        // then
//        verify(goalRepository, times(1)).findByYearAndMonth(anyInt(), anyInt());
//        verify(goalRepository, never()).save(any());
//
//        assertThat(exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
}