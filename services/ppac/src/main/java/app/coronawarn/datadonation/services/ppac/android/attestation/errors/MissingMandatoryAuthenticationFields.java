package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class MissingMandatoryAuthenticationFields extends RuntimeException {

  private static final long serialVersionUID = 6711968113382365103L;
  
  public MissingMandatoryAuthenticationFields(String message) {
    super(message);
  }
}
