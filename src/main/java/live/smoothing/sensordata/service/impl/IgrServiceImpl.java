package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.repository.IgrRepository;
import live.smoothing.sensordata.service.IgrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IgrServiceImpl implements IgrService {

    private final IgrRepository igrRepository;

    @Override
    public Igr getClassIgr() {

        return igrRepository.getClassIgr();
    }

    @Override
    public Igr getOfficeIgr() {

        return igrRepository.getOfficeIgr();
    }
}
