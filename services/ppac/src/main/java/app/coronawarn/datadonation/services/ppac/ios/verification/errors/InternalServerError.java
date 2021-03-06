package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

@SuppressWarnings("serial")
public class InternalServerError extends RuntimeException {

  public InternalServerError(final Throwable cause) {
    super("Internal error occurred: " + cause.getMessage(), cause);
  }

  InternalServerError(String msg, final Throwable cause) {
    super(msg, cause);
  }
}
