package app.coronawarn.datadonation.services.ppac.android.attestation.timestamp;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public final class NoOpTimestampVerificationStrategy implements TimestampVerificationStrategy {

  @Override
  public void validateTimestamp(long attestationTimestamp) {
    // do nothing during load testing
  }
}
