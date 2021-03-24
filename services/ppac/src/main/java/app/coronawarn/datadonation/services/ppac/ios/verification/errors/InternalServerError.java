package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class InternalServerError extends RuntimeException {

  public InternalServerError(final Throwable cause) {
    super("Internal error occurred: " + cause.getMessage(), cause);
  }
}
