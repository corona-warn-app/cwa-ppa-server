package app.coronawarn.analytics.services.ios.exception;

public class UnauthorizedException extends RuntimeException {

  private static final String ERROR_MESSAGE = "PPAC failed due to blocked device";

  public UnauthorizedException() {
    super(ERROR_MESSAGE);
  }
}
