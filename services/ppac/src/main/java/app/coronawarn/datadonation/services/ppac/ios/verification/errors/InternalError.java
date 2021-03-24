package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class InternalError extends RuntimeException {

  public InternalError(final Throwable cause) {
    super("Internal error occurred: " + cause.getMessage(), cause);
  }
}
