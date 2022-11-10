package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;

public interface AndroidIdVerificationStrategy {

  void validateAndroidId(final PPACAndroid androidId);
}
