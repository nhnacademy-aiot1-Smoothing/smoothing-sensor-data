package live.smoothing.sensordata.repository;

import live.smoothing.sensordata.dto.phase.Phase;

import java.util.List;

public interface ThreePhaseRepository {

    Phase getThreePhase(String[] topics);
}
