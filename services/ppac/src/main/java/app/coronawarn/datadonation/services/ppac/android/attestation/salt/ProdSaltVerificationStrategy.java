package app.coronawarn.datadonation.services.ppac.android.attestation.salt;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;
import app.coronawarn.datadonation.common.persistence.repository.ppac.android.SaltRepository;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.common.base.Strings;
import java.time.Instant;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdSaltVerificationStrategy implements SaltVerificationStrategy {

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
   *  Verify that the given salt has not been redeemed (expired).
   */
  public void validateSalt(String saltString) {
    if (Strings.isNullOrEmpty(saltString)) {
      throw new MissingMandatoryAuthenticationFields("No salt received");
    }
    saltRepository.findById(saltString).ifPresentOrElse(existingSalt -> {
      validateSaltCreationDate(existingSalt);
    }, () -> saltRepository.persist(saltString, Instant.now().toEpochMilli()));
  }

  private void validateSaltCreationDate(Salt existingSalt) {
    Integer attestationValidity = appParameters.getAndroid().getAttestationValidity();
    Instant present = Instant.now();
    Instant lowerLimit = present.minusSeconds(attestationValidity);
    Instant saltCreationDate = Instant.ofEpochMilli(existingSalt.getCreatedAt());
    if (!saltCreationDate.isAfter(lowerLimit)) {
      throw new SaltNotValidAnymore(existingSalt);
    }
  }
}
