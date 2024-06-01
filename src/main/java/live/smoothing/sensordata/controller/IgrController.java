package live.smoothing.sensordata.controller;

import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.service.IgrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensor/igr")
public class IgrController {

    private final IgrService igrService;

    @GetMapping("/class")
    public Igr getClassIgr() {
            return igrService.getClassIgr();
    }

    @GetMapping("/office")
    public Igr getOfficeIgr() {
        return igrService.getOfficeIgr();
    }
}
