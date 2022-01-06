package app.coronawarn.datadonation.services.edus.otp;

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
public class OtpControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger OTP_LOGGER = LoggerFactory.getLogger(OtpControllerExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void unknownException(Exception ex, WebRequest wr) {
    OTP_LOGGER.error("Unable to handle " + wr.getDescription(false), ex);
  }

  @ExceptionHandler(value = { OtpNotFoundException.class })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleNotFoundException(RuntimeException ex, WebRequest wr) {
    OTP_LOGGER.debug("Not found: {}", wr.getDescription(true));
  }
}
