package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class NonceCalculationError extends RuntimeException {

  private static final long serialVersionUID = -8400452065767821216L;

  public NonceCalculationError(Exception cause) {
    super("Could not recaculate nonce. ", cause);
  }

  public NonceCalculationError(String message) {
    super(message);
  }
}
