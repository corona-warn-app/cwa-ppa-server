package app.coronawarn.datadonation.services.ppac.android.attestation.salt;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.SaltData;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Profile("!loadtest")
public class ProdSaltVerificationStrategy implements SaltVerificationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProdSaltVerificationStrategy.class);

  private final SaltRepository saltRepository;
  private final PpacConfiguration appParameters;

  /**
   * Just constructs an instance.
   */
  public ProdSaltVerificationStrategy(SaltRepository saltRepository,
      PpacConfiguration appParameters) {
    this.saltRepository = saltRepository;
    this.appParameters = appParameters;
  }

  /**
   * Verify that the given salt has not been redeemed (expired).
   */
  public void validateSalt(String saltString) {
    LOGGER.debug("Salt received: {}", saltString);
    if (ObjectUtils.isEmpty(saltString)) {
      throw new MissingMandatoryAuthenticationFields("No salt received");
    }
    Optional<SaltData> saltOptional = saltRepository.findById(saltString);
    if (saltOptional.isPresent()) {
      validateSaltCreationDate(saltOptional.get());
    } else {
      saltRepository.persist(saltString, Instant.now().toEpochMilli());
    }
  }

  private void validateSaltCreationDate(SaltData existingSaltData) {
    Integer attestationValidity = appParameters.getAndroid().getAttestationValidity();
    Instant present = Instant.now();
    Instant lowerLimit = present.minusSeconds(attestationValidity);
    Instant saltCreationDate = Instant.ofEpochMilli(existingSaltData.getCreatedAt());
    if (!saltCreationDate.isAfter(lowerLimit)) {
      throw new SaltNotValidAnymore(existingSaltData);
    }
  }
}
