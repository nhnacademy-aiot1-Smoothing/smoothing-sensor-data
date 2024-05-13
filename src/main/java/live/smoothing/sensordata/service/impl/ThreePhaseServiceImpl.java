package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.phase.Phase;
import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.dto.phase.ThreePhase;
import live.smoothing.sensordata.repository.ThreePhaseRepository;
import live.smoothing.sensordata.service.ThreePhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreePhaseServiceImpl implements ThreePhaseService {

    private final ThreePhaseRepository phaseRepository;

    @Override
    public PhaseResponse getThreePhase() {
        Phase v1 =  phaseRepository.getThreePhase(new String[]{"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/1/de/v1"});
        Phase v12 =  phaseRepository.getThreePhase(new String[]{"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/1/de/v12"});
        ThreePhase v = new ThreePhase(v1, v12);

        Phase v2 = phaseRepository.getThreePhase(new String[]{"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/2/de/v2"});
        Phase v23 = phaseRepository.getThreePhase(new String[]{"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/2/de/v23"});
        ThreePhase vv = new ThreePhase(v2, v23);

        Phase v3 = phaseRepository.getThreePhase(new String[]{"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/3/de/v3"});
        Phase v31 = phaseRepository.getThreePhase(new String[]{"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/3/de/v31"});
        ThreePhase vvv = new ThreePhase(v3, v31);

        PhaseResponse resp = new PhaseResponse(v, vv, vvv);

        return resp;
    }
}
