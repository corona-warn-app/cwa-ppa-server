package app.coronawarn.datadonation.services.ppac.android.controller;

import static org.slf4j.LoggerFactory.getLogger;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Profile("test")
@ControllerAdvice
public class TestProfileExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void unknownException(final Exception ex, final WebRequest wr) {
    getLogger(getClass()).error("Unable to handle " + wr.getDescription(false), ex);
  }
}
