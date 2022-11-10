package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.repository.AndroidIdRepository;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPACAndroid;
import com.google.protobuf.ByteString;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  public static String pepper(final byte[] androidId, final byte[] pepper) {
    try {
      final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
      sha256.update(androidId);
      return Base64.getEncoder().encodeToString(sha256.digest(pepper));
    } catch (final NoSuchAlgorithmException e) {
      logger.error(e.getLocalizedMessage(), e);
    }
    return null;
  }

  public static String pepper(final ByteString androidId, final byte[] pepper) {
    return pepper(androidId.toByteArray(), pepper);
  }

  public static String pepper(final ByteString androidId, final String hexEncodedPepper) {
    return pepper(androidId.toByteArray(), HexFormat.of().parseHex(hexEncodedPepper));
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
  public void upsertAndroidId(final PPACAndroid ppacAndroid, final int expirationIntervalInDays, byte[] pepper) {
    // FIXME: How do we know that an exception occurred? The Optional can actually be empty, which would not be an
    // error...
    String androidId = pepper(ppacAndroid.getAndroidId(), pepper);
    final Optional<AndroidId> androidIdOptional = androidIdRepository.findById(androidId);
    final ZonedDateTime expirationDate = calculateExpirationDate(expirationIntervalInDays);
    if (androidIdOptional.isPresent()) {
      // update
      // FIXME: Same here: how do we catch exceptions here?? Can we simply catch DataAccessException for example?
      androidIdRepository.update(androidId, expirationDate.toInstant().toEpochMilli(), Instant.now().toEpochMilli());
    } else {
      // insert
      androidIdRepository.insert(androidId, expirationDate.toInstant().toEpochMilli(), Instant.now().toEpochMilli());
    }
  }
}
