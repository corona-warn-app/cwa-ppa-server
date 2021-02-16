package app.coronawarn.datadonation.services.ppac.android.attestation.salt;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadTestSaltVerificationStrategy implements SaltVerificationStrategy {

  @Override
  public void validateSalt(String saltString) {
    // skip this process during load testing
  }
}
