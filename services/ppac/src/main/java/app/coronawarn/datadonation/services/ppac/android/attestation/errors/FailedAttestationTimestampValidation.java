package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class FailedAttestationTimestampValidation extends RuntimeException {

  private static final long serialVersionUID = -8531642585453016232L;
  
  public FailedAttestationTimestampValidation() {
    super("JWS payload timestamp not in validity range");
  }
}
