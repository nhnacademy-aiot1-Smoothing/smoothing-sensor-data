package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, String> {

    @Query("select g from Goal g where year(g.goalDate) = ?1 and month(g.goalDate) = ?2")
    Goal findByYearAndMonth(Integer year, Integer month);

    @Query("select g from Goal g where year(g.goalDate) = ?1 order by g.goalDate")
    List<Goal> findAllByYear(Integer year);
}
