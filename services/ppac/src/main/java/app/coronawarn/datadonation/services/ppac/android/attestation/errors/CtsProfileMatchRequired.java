package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class CtsProfileMatchRequired extends RuntimeException {

  private static final long serialVersionUID = 3672399350081903614L;

  public CtsProfileMatchRequired(){
    super("Cts profile match required in Android attestation response");
  }
}
