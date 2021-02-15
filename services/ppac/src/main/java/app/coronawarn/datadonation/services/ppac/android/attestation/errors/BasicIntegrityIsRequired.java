package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class BasicIntegrityIsRequired extends RuntimeException {

  private static final long serialVersionUID = 2664915373178687868L;

  public BasicIntegrityIsRequired() {
    super("Basic Integrity is required in Android attestation response");
  }
}
