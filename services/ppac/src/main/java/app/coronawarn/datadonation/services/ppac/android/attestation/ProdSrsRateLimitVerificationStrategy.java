package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.DeviceQuoteExceeded;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdSrsRateLimitVerificationStrategy implements SrsRateLimitVerificationStrategy {

  private final PpacConfiguration appParameters;

  @Autowired
  private AndroidIdService androidIdService;

  /**
   * Just constructs an instance.
   */
  public ProdSrsRateLimitVerificationStrategy(final PpacConfiguration appParameters) {
    this.appParameters = appParameters;
  }

  private String calculatePepperedAndroidId(final ByteString androidId, final String pepper) {
    try {
      final byte[] hexedPepper = Hex.decodeHex(pepper);
      final byte[] androidIdBytes = androidId.toByteArray();

      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      outputStream.write(hexedPepper);
      outputStream.write(androidIdBytes);

      final byte[] concatenatedByteArray = outputStream.toByteArray();
      // FIXME: no idea what is really required here.
    } catch (DecoderException | IOException e) {
      // FIXME: what do we actually best do here? Do we simply return a 500 error here, with a custom exception?
      throw new RuntimeException("Error during processing");
    }
    // FIXME: return the correct value, not null
    return null;
  }

  private void isAndroidIdStillValid(final long lastUsedForSrsInMilliseconds,
      final int timeBetweenSubmissionsInDays) {
    final long timeIntervalInSeconds = timeBetweenSubmissionsInDays * 24L * 3600L;
    final Instant expirationDate = Instant.ofEpochMilli(lastUsedForSrsInMilliseconds)
        .plusSeconds(timeIntervalInSeconds);
    // TODO: is this condition correct, or do we need the opposite?
    if (expirationDate.isBefore(Instant.now())) {
      throw new DeviceQuoteExceeded();
    }
  }

  /**
   * Verify that the given android id does not violate the rate limit.
   */
  @Override
  public void validateSrsRateLimit(final ByteString androidIdByteString, final String pepper) {
    final String pepperedAndroidId = calculatePepperedAndroidId(androidIdByteString, pepper);
    final Optional<AndroidId> androidIdByPrimaryKey = androidIdService.getAndroidIdByPrimaryKey(pepperedAndroidId);
    if (androidIdByPrimaryKey.isPresent()) {
      final AndroidId androidId = androidIdByPrimaryKey.get();
      final long lastUsedForSrsInMilliseconds = androidId.getLastUsedSrs();
      isAndroidIdStillValid(lastUsedForSrsInMilliseconds, appParameters.getSrsTimeBetweenSubmissionsInDays());
    }

    throw new AndroidIdNotValid();
  }
}
