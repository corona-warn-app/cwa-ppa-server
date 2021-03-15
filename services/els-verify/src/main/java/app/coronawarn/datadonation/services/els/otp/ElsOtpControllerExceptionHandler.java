package app.coronawarn.datadonation.services.els.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpNotFoundException;
import app.coronawarn.datadonation.services.els.JsonParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ElsOtpControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(ElsOtpControllerExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void unknownException(Exception ex, WebRequest wr) {
    logger.error("Unable to handle " + wr.getDescription(false), ex);
  }

  @ExceptionHandler(value = { OtpNotFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleNotFoundException(RuntimeException ex, WebRequest wr) {
    logger.debug("Not found: " + wr.getDescription(true));
  }

  @ExceptionHandler(JsonParsingException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void jsonParsingException(RuntimeException ex, WebRequest wr) {
    logger.error("Unable to handle " + wr.getDescription(false), ex);
  }
}
