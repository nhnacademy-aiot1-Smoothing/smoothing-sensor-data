package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.phase.Phase;

import java.util.List;

public interface ThreePhaseRepository {

    List<Phase> getThreePhase(String[] topics);
}
