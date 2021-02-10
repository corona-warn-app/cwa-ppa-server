package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.services.ppac.domain.DataSubmissionResponse.of;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.API_TOKEN_EXPIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.API_TOKEN_QUOTA_EXCEEDED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.DEVICE_BLOCKED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.DEVICE_TOKEN_REDEEMED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.DEVICE_TOKEN_SYNTAX_ERROR;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.INTERNAL_SERVER_ERROR;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.services.ppac.domain.DataSubmissionResponse;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenAlreadyUsed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenExpired;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.ApiTokenQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceBlocked;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenRedeemed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.DeviceTokenSyntaxError;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class IosApiErrorHandler extends ResponseEntityExceptionHandler {

  private static final Map<Class<? extends RuntimeException>, PpacErrorState> ERROR_STATES =
      Map.of(ApiTokenAlreadyUsed.class, PpacErrorState.API_TOKEN_ALREADY_ISSUED,
          ApiTokenExpired.class, API_TOKEN_EXPIRED,
          DeviceTokenSyntaxError.class, DEVICE_TOKEN_SYNTAX_ERROR,
          ConstraintViolationException.class, DEVICE_TOKEN_SYNTAX_ERROR,
          DeviceBlocked.class, DEVICE_BLOCKED,
          DeviceTokenRedeemed.class, DEVICE_TOKEN_REDEEMED,
          ApiTokenQuotaExceeded.class, API_TOKEN_QUOTA_EXCEEDED,
          InternalError.class, INTERNAL_SERVER_ERROR);
  private SecurityLogger securityLogger;

  public IosApiErrorHandler(SecurityLogger securityLogger) {
    this.securityLogger = securityLogger;
  }

  @ExceptionHandler(value = {DeviceBlocked.class})
  protected ResponseEntity<Object> handleAuthenticationErrors(RuntimeException e,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(e);
    errorCode.secureLog(securityLogger, e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(of(errorCode));
  }

  @ExceptionHandler(value = {DeviceTokenSyntaxError.class, ConstraintViolationException.class})
  protected ResponseEntity<Object> handleBadRequests(RuntimeException e,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(e);
    errorCode.secureLog(securityLogger, e);
    return ResponseEntity.badRequest().body(of(errorCode));
  }

  @ExceptionHandler(value = {ApiTokenExpired.class, ApiTokenAlreadyUsed.class,
      DeviceTokenRedeemed.class})
  protected ResponseEntity<Object> handleForbiddenErrors(RuntimeException e,
      WebRequest webRequest) {

    final PpacErrorState errorCode = getErrorCode(e);
    errorCode.secureLog(securityLogger, e);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(of(errorCode));
  }

  @ExceptionHandler(value = {ApiTokenQuotaExceeded.class})
  protected ResponseEntity<DataSubmissionResponse> handleTooManyRequestsErrors(RuntimeException e,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(e);
    errorCode.secureLog(securityLogger, e);

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(of(errorCode));
  }

  @ExceptionHandler(value = {InternalError.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleInternalErrors(RuntimeException e,
      WebRequest webRequest) {
    getErrorCode(e).secureLog(securityLogger, e);
  }

  private PpacErrorState getErrorCode(RuntimeException runtimeException) {
    return ERROR_STATES.getOrDefault(runtimeException.getClass(), PpacErrorState.UNKNOWN);
  }
}
