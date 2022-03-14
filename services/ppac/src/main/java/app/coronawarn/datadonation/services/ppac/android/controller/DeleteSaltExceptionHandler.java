package app.coronawarn.datadonation.services.ppac.android.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Profile("test")
@ControllerAdvice
public class DeleteSaltExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = //NOSONAR logger naming
      LoggerFactory.getLogger(DeleteSaltExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void unknownException(Exception ex, WebRequest wr) {
    LOGGER.error("Unable to handle " + wr.getDescription(false), ex);
  }
}
