package app.coronawarn.datadonation.services.ppac.ios.exception;

public class ApiTokenAlreadyUsedException extends RuntimeException {

  public ApiTokenAlreadyUsedException() {
    super("PPAC failed due to API Token already issued this month");
  }
}
