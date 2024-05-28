package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 목표 데이터 레포지토리 인터페이스 JPA 구현체
 *
 * @author 박영준
 */
public interface GoalRepository extends JpaRepository<Goal, String> {

    /**
     * 년, 월에 해당하는 목표 데이터를 조회한다.
     *
     * @param year 연도
     * @param month 월
     * @return 목표 데이터
     */
    @Query("select g " +
            "from Goal g " +
            "where year(g.goalDate) = ?1 " +
            "and month(g.goalDate) = ?2")
    Optional<Goal> findByYearAndMonth(Integer year, Integer month);

    /**
     * 연도에 해당하는 목표 데이터를 조회한다.
     *
     * @param year 연도
     * @return 목표 데이터
     */
    @Query("select g " +
            "from Goal g " +
            "where year(g.goalDate) = ?1 " +
            "order by g.goalDate")
    List<Goal> findAllByYear(Integer year);
}
