package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class InternalServerError extends RuntimeException {

  private static final long serialVersionUID = 3035093883104293913L;

  public InternalServerError(final Throwable cause) {
    super("Internal error occurred: " + cause.getMessage(), cause);
  }

  InternalServerError(String msg, final Throwable cause) {
    super(msg, cause);
  }
}
