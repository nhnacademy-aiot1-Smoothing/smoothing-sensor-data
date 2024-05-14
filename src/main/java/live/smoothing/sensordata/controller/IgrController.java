package live.smoothing.sensordata.controller;

import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.service.IgrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        if(igrService.getClassIgr() != null) {
            return igrService.getClassIgr();
        } else {
            throw new CommonException(HttpStatus.NOT_FOUND,"Not Found");
        }
    }

    @GetMapping("/office")
    public Igr getOfficeIgr() {

        if(igrService.getOfficeIgr() != null) {
            return igrService.getOfficeIgr();
        } else {
            throw new CommonException(HttpStatus.NOT_FOUND,"Not Found");
        }
    }
}
