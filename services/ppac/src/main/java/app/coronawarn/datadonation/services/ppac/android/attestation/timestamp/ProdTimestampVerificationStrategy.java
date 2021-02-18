package app.coronawarn.datadonation.services.ppac.android.attestation.timestamp;

import static app.coronawarn.datadonation.common.utils.TimeUtils.isInRange;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.time.Instant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public final class ProdTimestampVerificationStrategy implements TimestampVerificationStrategy {

  private final PpacConfiguration appParameters;

  public ProdTimestampVerificationStrategy(PpacConfiguration config) {
    this.appParameters = config;
  }

  @Override
  public void validateTimestamp(long timestampMs) {
    Integer attestationValidity = appParameters.getAndroid().getAttestationValidity();
    Instant present = Instant.now();
    Instant upperLimit = present.plusSeconds(attestationValidity);
    Instant lowerLimit = present.minusSeconds(attestationValidity);
    if (!isInRange(timestampMs, lowerLimit, upperLimit)) {
      throw new FailedAttestationTimestampValidation();
    }
  }
}
