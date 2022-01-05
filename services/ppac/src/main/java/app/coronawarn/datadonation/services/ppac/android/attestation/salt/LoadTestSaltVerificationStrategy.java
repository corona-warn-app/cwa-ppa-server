package app.coronawarn.datadonation.services.ppac.android.attestation.salt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadTestSaltVerificationStrategy implements SaltVerificationStrategy {

  private static final Logger logger = LoggerFactory.getLogger(LoadTestSaltVerificationStrategy.class);

  @Override
  public void validateSalt(String saltString) {
    // skip this process during load testing
    logger.debug("Salt received: " + saltString);
  }
}
