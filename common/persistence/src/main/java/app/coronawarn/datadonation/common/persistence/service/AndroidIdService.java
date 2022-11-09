package app.coronawarn.datadonation.common.persistence.service;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.repository.AndroidIdRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AndroidIdService {

  @Autowired
  private AndroidIdRepository androidIdRepository;
  Logger logger = LoggerFactory.getLogger(AndroidIdService.class);
  // public static final int SECONDS_PER_DAY = 3600 * 24;

  private ZonedDateTime calculateExpirationDate(final int submissionIntervalInDays) {
    return ZonedDateTime.now(ZoneOffset.UTC).plusDays(submissionIntervalInDays);
  }

  public Optional<AndroidId> getAndroidIdByPrimaryKey(final String pk) {
    return androidIdRepository.findById(pk);
  }

  /**
   * Save a new Android ID.
   */
  public void upsertAndroidId(final String androidId, final Integer submissionIntervalInDays) {
    // FIXME: How do we know that an exception occurred? The Optional can actually be empty, which would not be an
    // error...
    final Optional<AndroidId> androidIdOptional = androidIdRepository.findById(androidId);
    final ZonedDateTime expirationDate = calculateExpirationDate(submissionIntervalInDays);
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
