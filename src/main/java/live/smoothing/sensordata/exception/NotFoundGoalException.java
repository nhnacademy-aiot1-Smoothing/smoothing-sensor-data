package live.smoothing.sensordata.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class NotFoundGoalException extends CommonException {
    public NotFoundGoalException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
