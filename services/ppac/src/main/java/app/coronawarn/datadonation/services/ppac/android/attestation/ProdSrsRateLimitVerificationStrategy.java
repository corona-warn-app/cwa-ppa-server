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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test && !loadtest")
public class ProdSrsRateLimitVerificationStrategy implements SrsRateLimitVerificationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProdSrsRateLimitVerificationStrategy.class);

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

  private void checkDeviceQuota(final AndroidId androidId) {
    if (isEmpty(androidId.getLastUsedSrs())) {
      return;
    }
    final Instant earliestNextSubmissionDate = ofEpochMilli(androidId.getLastUsedSrs())
        .plusSeconds(srsTimeBetweenSubmissionsInSeconds);
    if (earliestNextSubmissionDate.isAfter(now())) {
      LOGGER.debug("DeviceQuotaExceeded for '{}'", androidId);
      throw new DeviceQuotaExceeded();
    }
  }

  /**
   * Verify that the given android id does not violate the rate limit.
   */
  @Override
  public void validateSrsRateLimit(final byte[] androidId, final boolean acceptAndroidId) {
    final String pepperedAndroidId = pepper(androidId, pepper);
    final Optional<AndroidId> optional = androidIdService.getAndroidIdByPrimaryKey(pepperedAndroidId);
    if (optional.isPresent()) {
      checkDeviceQuota(optional.get());
    }
  }
}
