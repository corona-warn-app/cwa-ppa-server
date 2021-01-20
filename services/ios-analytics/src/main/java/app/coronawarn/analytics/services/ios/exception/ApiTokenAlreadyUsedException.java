package app.coronawarn.analytics.services.ios.exception;

public class ApiTokenAlreadyUsedException extends RuntimeException {
  
  public ApiTokenAlreadyUsedException() {
    super("PPAC failed due to API Token already issued this month");
  }
}
