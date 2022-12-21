package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class AndroidIdNotValid extends RuntimeException {

  private static final long serialVersionUID = -6556735715737848503L;


  public AndroidIdNotValid() {
    super("Android ID length is not 8 bytes");
  }
}
