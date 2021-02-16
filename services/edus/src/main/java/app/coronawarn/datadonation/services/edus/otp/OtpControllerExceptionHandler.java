package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class OtpControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(OtpControllerExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void unknownException(Exception ex, WebRequest wr) {
    logger.error("Unable to handle {}", wr.getDescription(false), ex);
  }

  @ExceptionHandler(value = {OtpNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  protected ResponseEntity<Object> handleNotFoundException(
      RuntimeException ex, WebRequest wr) {
    logger.error("Not found: {}", wr.getDescription(false), ex);
  }
}
