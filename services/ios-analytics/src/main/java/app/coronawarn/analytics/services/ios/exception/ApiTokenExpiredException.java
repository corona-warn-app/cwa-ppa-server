package app.coronawarn.analytics.services.ios.exception;

public class ApiTokenExpiredException extends RuntimeException {

  private static final String ERROR_MESSAGE = "PPAC failed du to expiration date";

  public ApiTokenExpiredException() {
    super(ERROR_MESSAGE);
  }
}
