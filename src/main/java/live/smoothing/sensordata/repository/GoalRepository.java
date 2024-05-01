package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.enttiy.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, String> {

    Goal findFirstByOrderByGoalDateDesc();

    @Query("select g from Goal g where year(g.goalDate) = ?1")
    List<Goal> findAllByYear(Integer year);
}
