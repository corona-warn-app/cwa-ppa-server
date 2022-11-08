package app.coronawarn.datadonation.services.ppac.android.attestation;

import com.google.protobuf.ByteString;

public interface AndroidIdVerificationStrategy {

  void validateAndroidId(ByteString androidId);
}
