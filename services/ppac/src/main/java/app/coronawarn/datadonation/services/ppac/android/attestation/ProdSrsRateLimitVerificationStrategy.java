package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
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
   * Pepper for encryption.
   *
   * @see Android#getAndroidIdPepper()
   */
  private final byte[] pepper;

  /**
   * Minimum time in seconds between two self reports.
   *
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
    if (ObjectUtils.isEmpty(lastUsedForSrsInMilliseconds)) {
      return;
    }
    final Instant earliestNextSubmissionDate = Instant.ofEpochMilli(lastUsedForSrsInMilliseconds)
            .plusSeconds(srsTimeBetweenSubmissionsInSeconds);
    if (earliestNextSubmissionDate.isAfter(Instant.now())) {
      throw new DeviceQuotaExceeded();
    }
  }

  /**
   * Verify that the given android id does not violate the rate limit.
   */
  @Override
  public void validateSrsRateLimit(final byte[] androidId) {
    final String pepperedAndroidId = AndroidIdService.pepper(androidId, pepper);
    final Optional<AndroidId> optional = androidIdService.getAndroidIdByPrimaryKey(pepperedAndroidId);

    if (optional.isPresent()) {
      final AndroidId dbAndroidId = optional.get();
      final Long lastUsedForSrsInMilliseconds = dbAndroidId.getLastUsedSrs();
      checkDeviceQuota(lastUsedForSrsInMilliseconds);
    }
  }
}
