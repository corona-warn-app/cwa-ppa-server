package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.services.ios.exception.ApiTokenAlreadyUsedException;
import app.coronawarn.analytics.services.ios.exception.ApiTokenExpiredException;
import app.coronawarn.analytics.services.ios.exception.BadDeviceTokenException;
import app.coronawarn.analytics.services.ios.exception.InternalErrorException;
import app.coronawarn.analytics.services.ios.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);


  @ExceptionHandler(value = {UnauthorizedException.class})
  protected ResponseEntity<Object> handleBlockedDevice(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn(runtimeException.getMessage());
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, webRequest);

  }

  @ExceptionHandler(value = {BadDeviceTokenException.class})
  protected ResponseEntity<Object> handleBadDeviceToken(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn(runtimeException.getMessage());
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

  }


  @ExceptionHandler(value = {ApiTokenExpiredException.class, ApiTokenAlreadyUsedException.class})
  protected ResponseEntity<Object> handleApiTokenExpired(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn(runtimeException.getMessage());
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.FORBIDDEN, webRequest);

  }

  @ExceptionHandler(value = {InternalErrorException.class})
  protected ResponseEntity<Object> handleInternalErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
        webRequest);

  }
}
