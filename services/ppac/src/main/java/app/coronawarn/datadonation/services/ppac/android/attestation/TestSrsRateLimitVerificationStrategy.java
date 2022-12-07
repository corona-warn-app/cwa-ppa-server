package app.coronawarn.datadonation.services.ppac.android.attestation;

import static app.coronawarn.datadonation.common.persistence.service.AndroidIdService.pepper;
import static java.time.Instant.now;
import static java.time.Instant.ofEpochMilli;
import static org.springframework.util.ObjectUtils.isEmpty;

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

@Component
@Profile("test || loadtest")
public class TestSrsRateLimitVerificationStrategy implements SrsRateLimitVerificationStrategy {

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
  public TestSrsRateLimitVerificationStrategy(final PpacConfiguration appParameters) {
    pepper = appParameters.getAndroid().pepper();
    srsTimeBetweenSubmissionsInSeconds = appParameters.getSrsTimeBetweenSubmissionsInDays() * 24L * 3600L;
  }

  private void checkDeviceQuota(final Long lastUsedForSrsInMilliseconds, final boolean acceptAndroidId) {
    if (isEmpty(lastUsedForSrsInMilliseconds)) {
      return;
    }
    final Instant earliestNextSubmissionDate = ofEpochMilli(lastUsedForSrsInMilliseconds)
        .plusSeconds(srsTimeBetweenSubmissionsInSeconds);
    if (earliestNextSubmissionDate.isAfter(now()) && !acceptAndroidId) {
      throw new DeviceQuotaExceeded();
    }
  }

  /**
   * Verify that the given android id does not violate the rate limit.
   */
  @Override
  public void validateSrsRateLimit(final byte[] androidId) {
    final String pepperedAndroidId = pepper(androidId, pepper);
    final Optional<AndroidId> dbAndroidId = androidIdService.getAndroidIdByPrimaryKey(pepperedAndroidId);

    if (dbAndroidId.isPresent()) {
      final Long lastUsedForSrsInMilliseconds = dbAndroidId.get().getLastUsedSrs();
      checkDeviceQuota(lastUsedForSrsInMilliseconds, true /* FIXME pass in as argument */);
    }
  }
}
