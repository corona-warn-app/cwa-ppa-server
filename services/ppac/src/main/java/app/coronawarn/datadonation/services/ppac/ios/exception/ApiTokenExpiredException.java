package app.coronawarn.datadonation.services.ppac.ios.exception;

public class ApiTokenExpiredException extends RuntimeException {

  public ApiTokenExpiredException() {
    super("PPAC failed du to expiration date");
  }
}
