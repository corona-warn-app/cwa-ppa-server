package app.coronawarn.datadonation.services.ppac.commons.web;

import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorState.INTERNAL_SERVER_ERROR;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.persistence.errors.MetricsDataCouldNotBeStored;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;
import java.util.Map;
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

  private static final Map<Class<? extends RuntimeException>, PpacErrorState> ERROR_STATES =
      Map.of(MetricsDataCouldNotBeStored.class, INTERNAL_SERVER_ERROR,
             InternalError.class, INTERNAL_SERVER_ERROR);

  @ExceptionHandler(value = {InternalError.class, MetricsDataCouldNotBeStored.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleInternalErrors(RuntimeException e, WebRequest webRequest) {
    getErrorCode(e).secureLog(securityLogger, e);
  }

  private PpacErrorState getErrorCode(RuntimeException runtimeException) {
    return ERROR_STATES.getOrDefault(runtimeException.getClass(), PpacErrorState.UNKNOWN);
  }
}