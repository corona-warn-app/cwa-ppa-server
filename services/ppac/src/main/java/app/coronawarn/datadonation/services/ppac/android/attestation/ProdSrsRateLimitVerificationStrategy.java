package app.coronawarn.datadonation.services.ppac.android.attestation;

import static app.coronawarn.datadonation.common.persistence.service.AndroidIdService.pepper;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.DeviceQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Profile("!loadtest")
public class ProdSrsRateLimitVerificationStrategy implements SrsRateLimitVerificationStrategy {

  /**
   * @see Android#getAndroidIdPepper()
   */
  private final byte[] pepper;

  /**
   * @see PpacConfiguration#getSrsTimeBetweenSubmissionsInDays()
   */
  private final long srsTimeBetweenSubmissionsInSeconds;

  @Autowired
  private AndroidIdService androidIdService;

  /**
   * Just constructs an instance.
   */
  public ProdSrsRateLimitVerificationStrategy(final PpacConfiguration appParameters) {
    pepper = appParameters.getAndroid().pepper();
    srsTimeBetweenSubmissionsInSeconds = appParameters.getSrsTimeBetweenSubmissionsInDays() * 24L * 3600L;
  }

  private void checkDeviceQuota(final Long lastUsedForSrsInMilliseconds) {
    if (ObjectUtils.isEmpty(lastUsedForSrsInMilliseconds))
      return;
    final Instant expirationDate = Instant.ofEpochMilli(lastUsedForSrsInMilliseconds)
        .plusSeconds(srsTimeBetweenSubmissionsInSeconds);
    if (expirationDate.isAfter(Instant.now())) {
      throw new DeviceQuotaExceeded();
    }
  }

  /**
   * Verify that the given android id does not violate the rate limit.
   */
  @Override
  public void validateSrsRateLimit(final PPACAndroid ppacAndroid) {
    final String pepperedAndroidId = pepper(ppacAndroid.getAndroidId(), pepper);
    final Optional<AndroidId> optional = androidIdService.getAndroidIdByPrimaryKey(pepperedAndroidId);

    if (optional.isPresent()) {
      final AndroidId androidId = optional.get();
      final Long lastUsedForSrsInMilliseconds = androidId.getLastUsedSrs();
      checkDeviceQuota(lastUsedForSrsInMilliseconds);
    }
  }
}
