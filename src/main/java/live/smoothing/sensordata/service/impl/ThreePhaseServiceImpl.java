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
    private final String CLASS_LL = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ll_average";
    private final String CLASS_LN = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ln_average";
    private final String OFFICE_LL = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ll_average";
    private final String OFFICE_LN = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ln_average";
    @Override
    public PhaseResponse getThreePhase() {
        Phase class_ll =  phaseRepository.getThreePhase(new String[]{CLASS_LL});
        Phase class_ln =  phaseRepository.getThreePhase(new String[]{CLASS_LN});
        ThreePhase classA = new ThreePhase(class_ll, class_ln);

        Phase office_ll = phaseRepository.getThreePhase(new String[]{OFFICE_LL});
        Phase office_ln = phaseRepository.getThreePhase(new String[]{OFFICE_LN});
        ThreePhase office = new ThreePhase(office_ll, office_ln);

        PhaseResponse resp = new PhaseResponse(classA, office);

        return resp;
    }
}
