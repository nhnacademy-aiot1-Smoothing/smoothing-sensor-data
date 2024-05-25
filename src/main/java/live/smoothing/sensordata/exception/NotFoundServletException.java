package live.smoothing.sensordata.exception;

import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class NotFoundServletException extends CommonException {

    public NotFoundServletException(HttpStatus status, String errorMessage) {
        super(status, errorMessage);
    }
}
