package app.coronawarn.datadonation.services.ppac.android.attestation.timestamp;

public interface TimestampVerificationStrategy {

  void validateTimestamp(long attestationTimestamp);
}
