package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class InternalError extends RuntimeException {

  public InternalError(String msg) {
    super(msg);
  }

  public InternalError(String msg, Exception e) {
    super(msg, e);
  }
}
