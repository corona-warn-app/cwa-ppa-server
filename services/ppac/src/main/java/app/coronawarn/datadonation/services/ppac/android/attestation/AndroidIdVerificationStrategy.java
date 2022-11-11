package app.coronawarn.datadonation.services.ppac.android.attestation;

public interface AndroidIdVerificationStrategy {

  void validateAndroidId(final byte[] androidId);
}
