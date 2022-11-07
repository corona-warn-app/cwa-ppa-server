package app.coronawarn.datadonation.services.srs.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SrsOtpControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = //NOSONAR logger naming
      LoggerFactory.getLogger(SrsOtpControllerExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void unknownException(Exception ex, WebRequest wr) {
    LOGGER.error("Unable to handle " + wr.getDescription(false), ex);
  }

  @ExceptionHandler(value = { OtpNotFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleNotFoundException(RuntimeException ex, WebRequest wr) {
    LOGGER.debug("Not found: {}", wr.getDescription(true));
  }
}
