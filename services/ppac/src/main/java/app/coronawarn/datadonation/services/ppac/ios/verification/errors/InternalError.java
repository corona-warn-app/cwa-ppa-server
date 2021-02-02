package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class InternalError extends RuntimeException {

  public InternalError(Exception e) {
    super(e.getMessage(), e.getCause());
  }
}
