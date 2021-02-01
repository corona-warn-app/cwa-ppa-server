package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.services.ppac.ios.verification.DataSubmissionResponse.of;
import static app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosErrorState.API_TOKEN_ALREADY_ISSUED;
import static app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosErrorState.API_TOKEN_EXPIRED;
import static app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosErrorState.API_TOKEN_QUOTA_EXCEEDED;
import static app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosErrorState.DEVICE_BLOCKED;
import static app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosErrorState.DEVICE_TOKEN_REDEEMED;
import static app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosErrorState.DEVICE_TOKEN_SYNTAX_ERROR;

import app.coronawarn.datadonation.services.ppac.ios.verification.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenExpired;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.BadDeviceToken;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceBlocked;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenRedeemed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.EdusAlreadyAccessed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import javax.validation.ConstraintViolationException;
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
public class IosApiErrorHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

  @ExceptionHandler(value = {DeviceBlocked.class})
  protected ResponseEntity<Object> handleBlockedDevice(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn("Security Warning: " + runtimeException.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(of(DEVICE_BLOCKED));

  }

  @ExceptionHandler(value = {BadDeviceToken.class, ConstraintViolationException.class})
  protected ResponseEntity<Object> handleBadDeviceToken(RuntimeException runtimeException,
      WebRequest webRequest) {
    if (runtimeException instanceof BadDeviceToken) {
      logger.warn(runtimeException.getMessage());
    }
    return ResponseEntity.badRequest().body(of(DEVICE_TOKEN_SYNTAX_ERROR));
  }

  @ExceptionHandler(value = {ApiTokenExpired.class, ApiTokenAlreadyUsed.class,
      DeviceTokenRedeemed.class})
  protected ResponseEntity<Object> handleApiTokenExpired(RuntimeException runtimeException,
      WebRequest webRequest) {
    if (runtimeException instanceof DeviceTokenRedeemed) {
      logger.warn("Security Warning:" + runtimeException.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(of(DEVICE_TOKEN_REDEEMED));
    }
    if (runtimeException instanceof ApiTokenAlreadyUsed) {
      logger.warn(runtimeException.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(of(API_TOKEN_ALREADY_ISSUED));
    }

    if (runtimeException instanceof ApiTokenExpired) {
      logger.warn("Security Warning: " + runtimeException.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(of(API_TOKEN_EXPIRED));
    }
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.FORBIDDEN, webRequest);
  }

  @ExceptionHandler(value = {EdusAlreadyAccessed.class})
  protected ResponseEntity<DataSubmissionResponse> handleEdusAlreadyAccessed(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn("Security Warning: " + runtimeException.getMessage());
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(of(API_TOKEN_QUOTA_EXCEEDED));

  }

  @ExceptionHandler(value = {InternalError.class})
  protected ResponseEntity<Object> handleInternalErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.error(runtimeException.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
