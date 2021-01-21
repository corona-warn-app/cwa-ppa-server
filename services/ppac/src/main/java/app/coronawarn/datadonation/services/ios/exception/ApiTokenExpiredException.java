package app.coronawarn.datadonation.services.ios.exception;

public class ApiTokenExpiredException extends RuntimeException {

  public ApiTokenExpiredException() {
    super("PPAC failed du to expiration date");
  }
}
