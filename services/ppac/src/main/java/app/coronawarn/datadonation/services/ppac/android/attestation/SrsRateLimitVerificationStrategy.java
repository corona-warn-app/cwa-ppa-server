package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.protobuf.ByteString;

public interface SrsRateLimitVerificationStrategy {

  void validateSrsRateLimit(ByteString androidId, String pepper);
}
