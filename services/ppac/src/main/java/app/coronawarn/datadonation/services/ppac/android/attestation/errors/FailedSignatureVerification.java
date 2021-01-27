package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public final class FailedSignatureVerification extends RuntimeException {

  private static final long serialVersionUID = 5078963545906035590L;
  
  public FailedSignatureVerification(String message) {
    super(message);
  }
  
  public FailedSignatureVerification(String message, Throwable e) {
    super(message, e);
  }
}
