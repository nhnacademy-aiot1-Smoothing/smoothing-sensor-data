package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.phase.Phase;
import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.dto.phase.ThreePhase;
import live.smoothing.sensordata.repository.ThreePhaseRepository;
import live.smoothing.sensordata.service.ThreePhaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreePhaseServiceImpl implements ThreePhaseService {

    private final ThreePhaseRepository phaseRepository;

    @Override
    public PhaseResponse getThreePhase() {
        List<Phase> v1 =  phaseRepository.getThreePhase(new String[]{"v1"});
        List<Phase> v12 =  phaseRepository.getThreePhase(new String[]{"v12"});
        ThreePhase v = new ThreePhase(v1, v12);

        List<Phase> v2 = phaseRepository.getThreePhase(new String[]{"v2"});
        List<Phase> v23 = phaseRepository.getThreePhase(new String[]{"v23"});
        ThreePhase vv = new ThreePhase(v2, v23);

        List<Phase> v3 = phaseRepository.getThreePhase(new String[]{"v3"});
        List<Phase> v31 = phaseRepository.getThreePhase(new String[]{"v31"});
        ThreePhase vvv = new ThreePhase(v3, v31);

        PhaseResponse resp = new PhaseResponse(v, vv, vvv);
        log.error("time: {}", resp.getFirst().getTop().get(0).getTime().toString());
        log.error("value: {}", resp.getFirst().getTop().get(0).getValue().toString());

        log.error("time: {}", resp.getSecond().getTop().get(0).getTime().toString());
        log.error("value: {}", resp.getSecond().getTop().get(0).getValue().toString());

        log.error("time: {}", resp.getThird().getTop().get(0).getTime().toString());
        log.error("value: {}", resp.getThird().getTop().get(0).getValue().toString());
        return resp;
    }
}
