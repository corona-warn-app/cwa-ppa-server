package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.services.ios.exception.BlockedDeviceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class IosExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BlockedDeviceException.class})
    protected ResponseEntity<Object> handleBlockedDevice(RuntimeException runtimeException,
                                                         WebRequest webRequest) {
        return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, webRequest);

    }
}
