package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.repository.AndroidIdRepository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class AndroidIdService {

  private static Logger logger = LoggerFactory.getLogger(AndroidIdService.class);

  /**
   * Encrypts the Android ID.
   *
   * @param androidId the Android ID, which should be obfuscated
   * @param pepper    the pepper to use for obfuscation
   * @return Sha-256 has sum as base64 encoded string
   */
  @NonNull
  public static String pepper(@NonNull final byte[] androidId, @NonNull final byte[] pepper) {
    try {
      final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      sha256.update(androidId);
      return Base64.getEncoder().encodeToString(sha256.digest(pepper));
    } catch (final NoSuchAlgorithmException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
    return null;
  }

  @Autowired
  private AndroidIdRepository androidIdRepository;

  private ZonedDateTime calculateExpirationDate(final int submissionIntervalInDays) {
    return ZonedDateTime.now(ZoneOffset.UTC).plusDays(submissionIntervalInDays);
  }

  public Optional<AndroidId> getAndroidIdByPrimaryKey(final String pk) {
    return androidIdRepository.findById(pk);
  }

  /**
   * Save a new Android ID.
   */
  public void upsertAndroidId(final byte[] androidId, final int expirationIntervalInDays, final byte[] pepper) {
    final String pepperedAndroidId = pepper(androidId, pepper);
    final Optional<AndroidId> androidIdOptional = androidIdRepository.findById(pepperedAndroidId);
    final ZonedDateTime expirationDate = calculateExpirationDate(expirationIntervalInDays);
    if (androidIdOptional.isPresent()) {
      // FIXME: how do we catch exceptions here?? Can we simply catch DataAccessException for example?
      androidIdRepository.update(pepperedAndroidId, expirationDate.toInstant().toEpochMilli(),
          Instant.now().toEpochMilli());
    } else {
      androidIdRepository.insert(pepperedAndroidId, expirationDate.toInstant().toEpochMilli(),
          Instant.now().toEpochMilli());
    }
  }
}
