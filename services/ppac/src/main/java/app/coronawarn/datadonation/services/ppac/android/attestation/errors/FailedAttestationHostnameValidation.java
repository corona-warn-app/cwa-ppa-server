package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public final class FailedAttestationHostnameValidation extends RuntimeException {

  private static final long serialVersionUID = -8531642585453016232L;

  public FailedAttestationHostnameValidation(String message) {
    super(message);
  }
}
