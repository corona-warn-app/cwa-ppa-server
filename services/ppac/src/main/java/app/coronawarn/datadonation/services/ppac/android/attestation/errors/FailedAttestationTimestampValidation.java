package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class FailedAttestationTimestampValidation extends RuntimeException {

  private static final long serialVersionUID = -8531642585453016232L;
  private static final String ERROR_MESSAGE = "JWS payload timestamp not in validity range";

  public FailedAttestationTimestampValidation() {
    super(ERROR_MESSAGE);
  }
}
