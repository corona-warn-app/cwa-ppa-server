package app.coronawarn.datadonation.services.ppac.android.attestation;

public interface SrsRateLimitVerificationStrategy {

  void validateSrsRateLimit(final byte[] androidId, final boolean acceptAndroidId);
}
