package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class NonceCouldNotBeVerified extends RuntimeException {

  private static final long serialVersionUID = -288077614896557469L;

  public NonceCouldNotBeVerified(String message) {
    super(message);
  }
  
  public NonceCouldNotBeVerified(String message, Exception cause) {
    super(message, cause);
  }
}
