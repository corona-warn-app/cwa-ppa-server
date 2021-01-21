package app.coronawarn.analytics.services.ios.exception;

public class UnauthorizedException extends RuntimeException {
  
  public UnauthorizedException() {
    super("PPAC failed due to blocked device");
  }
}
