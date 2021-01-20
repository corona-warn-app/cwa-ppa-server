package app.coronawarn.analytics.services.ios.exception;

public class ApiTokenAlreadyUsedException extends RuntimeException {

  private static final String ERROR_MESSAGE = "PPAC failed due to API Token already issued this month";

  public ApiTokenAlreadyUsedException() {
    super(ERROR_MESSAGE);
  }
}
