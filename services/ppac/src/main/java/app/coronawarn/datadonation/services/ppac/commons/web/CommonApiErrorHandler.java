package app.coronawarn.datadonation.services.ppac.commons.web;

import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.INTERNAL_SERVER_ERROR;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestValidationFailed;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import java.util.Map;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CommonApiErrorHandler extends ResponseEntityExceptionHandler {

  private SecurityLogger securityLogger;

  public CommonApiErrorHandler(SecurityLogger securityLogger) {
    this.securityLogger = securityLogger;
  }

  private static final Map<Class<? extends RuntimeException>, PpacErrorCode> ERROR_CODES = Map.of(
      MetricsDataCouldNotBeStored.class, PpacErrorCode.METRICS_DATA_NOT_VALID, PpaDataRequestValidationFailed.class,
      PpacErrorCode.METRICS_DATA_NOT_VALID, InternalError.class, INTERNAL_SERVER_ERROR);

  @ExceptionHandler(value = { InternalError.class })
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleInternalErrors(RuntimeException e, WebRequest webRequest) {
    getErrorCode(e).secureLog(securityLogger, e);
  }

  @ExceptionHandler(value = { MetricsDataCouldNotBeStored.class, PpaDataRequestValidationFailed.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected void handleBadRequest(RuntimeException e, WebRequest webRequest) {
    getErrorCode(e).secureLog(securityLogger, e);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Object exceptionHandler(RuntimeException e) {
    securityLogger.error(e);
    return null;
  }

  @ExceptionHandler(ClientAbortException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Object exceptionHandler(ClientAbortException exc) {
    return null; //socket is closed, cannot return any response    
  }

  private PpacErrorCode getErrorCode(RuntimeException runtimeException) {
    return ERROR_CODES.getOrDefault(runtimeException.getClass(), PpacErrorCode.UNKNOWN);
  }
}