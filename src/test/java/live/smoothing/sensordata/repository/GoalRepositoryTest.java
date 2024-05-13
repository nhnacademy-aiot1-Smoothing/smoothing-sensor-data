package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class GoalRepositoryTest {

    @Autowired
    private GoalRepository goalRepository;

    @BeforeEach
    void setUp() {

        // 2021-01-01 년도를 LocalDateTime으로 생성
        Goal firstGoal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(3000.0)
                .unitPrice(300)
                .build();

        Goal secondGoal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 2, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(2000.0)
                .unitPrice(200)
                .build();

        Goal thirdGoal = Goal.builder()
                .goalDate(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalDateTime.MIN.toLocalTime()))
                .goalAmount(1000.2)
                .unitPrice(100)
                .build();

        goalRepository.save(firstGoal);
        goalRepository.save(secondGoal);
        goalRepository.save(thirdGoal);
    }

    @Test
    @DisplayName("가장 최근 튜플을 가지고 온다.")
    void findFirstByOrderByGoalDateDesc() {
        // given
        // when
        Goal findFirst = goalRepository.findFirstByOrderByGoalDateDesc();

        // then
        assertAll(
                () -> assertThat(findFirst.getGoalDate().getYear()).isEqualTo(2024),
                () -> assertThat(findFirst.getGoalDate().getMonthValue()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("해당 년도의 목표들을 가지고 온다.")
    void findAllByYear() {
        // given
        Integer year = 2021;

        // when
        goalRepository.findAllByYear(year);

        // then
        assertThat(goalRepository.findAllByYear(year)).hasSize(3);
    }
}