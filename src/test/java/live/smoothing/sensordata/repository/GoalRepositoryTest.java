package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
    @DisplayName("지정한 연도와 월에 해당하는 목표를 가지고온다.")
    void findFirstByOrderByGoalDateDesc() {
        // given
        // when
        Goal findFirst = goalRepository.findByYearAndMonth(2021, 3)
                .orElse(null);

        // then
        assertAll(
                () -> assertThat(Objects.requireNonNull(findFirst).getGoalDate().getYear()).isEqualTo(2021),
                () -> assertThat(Objects.requireNonNull(findFirst).getGoalDate().getMonthValue()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("지정한 연도와 월에 해당하는 목표가 없으면 null을 반환한다.")
    void notfound_findFirstByOrderByGoalDateDesc() {
        // given
        // when
        Goal findFirst = goalRepository.findByYearAndMonth(2999, 1)
                .orElse(null);

        // then
        assertThat(findFirst).isNull();
    }

    @Test
    @DisplayName("해당 년도에 목표들이 존재한다면 목표들을 가지고 온다.")
    void findAllByYear() {
        // given
        Integer year = 2021;

        // when
        goalRepository.findAllByYear(year);

        // then
        assertThat(goalRepository.findAllByYear(year)).hasSize(3);
    }

    @Test
    @DisplayName("해당 년도의 목표들이 존재하지 않는다면 빈 목표 리스트를 반환한다.")
    void not_exists_findAllByYear() {
        // given
        Integer year = 2999;

        // when
        goalRepository.findAllByYear(year);

        // then
        assertThat(goalRepository.findAllByYear(year)).isEmpty();
    }
}