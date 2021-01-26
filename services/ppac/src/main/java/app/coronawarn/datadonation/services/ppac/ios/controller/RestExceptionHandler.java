package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.services.ppac.ios.exception.*;
import app.coronawarn.datadonation.services.ppac.ios.identification.DataSubmissionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.validation.ConstraintViolationException;

import static app.coronawarn.datadonation.services.ppac.config.PpacErrorState.*;
import static app.coronawarn.datadonation.services.ppac.ios.identification.DataSubmissionResponse.of;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

  @ExceptionHandler(value = {DeviceBlockedException.class})
  protected ResponseEntity<Object> handleBlockedDevice(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn("Security Warning: " + runtimeException.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(of(DEVICE_BLOCKED));

  }

  @ExceptionHandler(value = {BadDeviceTokenException.class, ConstraintViolationException.class})
  protected ResponseEntity<Object> handleBadDeviceToken(RuntimeException runtimeException,
      WebRequest webRequest) {
    if (runtimeException instanceof BadDeviceTokenException) {
      logger.warn(runtimeException.getMessage());
    }
    return ResponseEntity.badRequest().body(of(DEVICE_TOKEN_SYNTAX_ERROR));
  }

  @ExceptionHandler(value = {ApiTokenExpiredException.class, ApiTokenAlreadyUsedException.class,
      DeviceTokenRedeemedException.class})
  protected ResponseEntity<Object> handleApiTokenExpired(RuntimeException runtimeException,
      WebRequest webRequest) {
    if (runtimeException instanceof DeviceTokenRedeemedException) {
      logger.warn("Security Warning:" + runtimeException.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(of(DEVICE_TOKEN_REDEEMED));
    }
    if (runtimeException instanceof ApiTokenAlreadyUsedException) {
      logger.warn(runtimeException.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(of(API_TOKEN_ALREADY_ISSUED));
    }

    if (runtimeException instanceof ApiTokenExpiredException) {
      logger.warn("Security Warning: " + runtimeException.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(of(API_TOKEN_EXPIRED));
    }
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.FORBIDDEN, webRequest);
  }

  @ExceptionHandler(value = {EdusAlreadyAccessedException.class})
  protected ResponseEntity<DataSubmissionResponse> handleEdusAlreadyAccessed(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn("Security Warning: " + runtimeException.getMessage());
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(of(API_TOKEN_QUOTA_EXCEEDED));

  }

  @ExceptionHandler(value = {InternalErrorException.class})
  protected ResponseEntity<Object> handleInternalErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.error(runtimeException.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
