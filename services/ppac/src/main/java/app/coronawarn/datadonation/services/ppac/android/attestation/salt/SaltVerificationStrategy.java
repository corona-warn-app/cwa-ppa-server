package app.coronawarn.datadonation.services.ppac.android.attestation.salt;

public interface SaltVerificationStrategy {

  void validateSalt(String saltString);  
}
