package live.smoothing.sensordata.advice;

import live.smoothing.common.dto.ErrorResponse;
import live.smoothing.common.exception.CommonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(HttpServletRequest request, CommonException e) {
        return ResponseEntity.status(e.getStatus())
                .body(e.toEntity(request.getServletPath()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentConversionNotSupportedException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .errorMessage("파라미터 타입이 잘못되었습니다.")
                                .path(request.getServletPath())
                                .build()
                );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .errorMessage("필수 파라미터가 누락되었습니다.")
                                .path(request.getServletPath())
                                .build()
                );
    }
}
