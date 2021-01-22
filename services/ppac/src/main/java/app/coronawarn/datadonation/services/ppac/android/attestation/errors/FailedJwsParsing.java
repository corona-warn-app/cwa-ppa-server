package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public final class FailedJwsParsing extends RuntimeException {
  
  private static final long serialVersionUID = -1579569743042757810L;
  
  public static final String MESSAGE = "Invalid JWS format received";
  
  public FailedJwsParsing() {
    super(MESSAGE);
  }
}
