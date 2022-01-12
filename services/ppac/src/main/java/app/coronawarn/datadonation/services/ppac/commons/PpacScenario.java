package app.coronawarn.datadonation.services.ppac.commons;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement;
import app.coronawarn.datadonation.services.ppac.android.attestation.PpacAndroidIntegrityValidator;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosScenarioRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit.PpacIosRateLimitStrategy;
import java.util.function.BiConsumer;

public enum PpacScenario {

  EDUS(PpacIosRateLimitStrategy::validateForEdus, PpacIosScenarioRepository::saveForEdus,
      PpacIosScenarioRepository::updateForEdus, PpacAndroidIntegrityValidator::validateIntegrityForEdus),
  PPA(PpacIosRateLimitStrategy::validateForPpa, PpacIosScenarioRepository::saveForPpa,
      PpacIosScenarioRepository::updateForPpa, PpacAndroidIntegrityValidator::validateIntegrityForPpa),

  /**
   * For ELS scenario, only the integriyValidator is required. The API token operations are skipped since no API token
   * is required to use this scenario.
   */
  LOG((what, ever) -> {}, (what, ever) -> {}, (what, ever) -> {},
      PpacAndroidIntegrityValidator::validateIntegrityForEls);

  private final BiConsumer<PpacIosRateLimitStrategy, ApiTokenData> validationCommand;
  private final BiConsumer<PpacIosScenarioRepository, ApiTokenData> insertCommand;
  private final BiConsumer<PpacIosScenarioRepository, ApiTokenData> updateCommand;
  private final BiConsumer<PpacAndroidIntegrityValidator, AttestationStatement> integrityValidator;

  PpacScenario(BiConsumer<PpacIosRateLimitStrategy, ApiTokenData> validationCommand,
      BiConsumer<PpacIosScenarioRepository, ApiTokenData> insertCommand,
      BiConsumer<PpacIosScenarioRepository, ApiTokenData> updateCommand,
      BiConsumer<PpacAndroidIntegrityValidator, AttestationStatement> integrityValidator) {
    this.validationCommand = validationCommand;
    this.insertCommand = insertCommand;
    this.updateCommand = updateCommand;
    this.integrityValidator = integrityValidator;
  }

  /**
   * Calls the validate method of the provided Validator with the provided API Token as the input, corresponding to the
   * current scenario.
   *
   * @param validator {@link PpacIosRateLimitStrategy} which validates the provided API Token.
   * @param apiTokenData  {@link ApiTokenData} that is to be validated.
   */
  public void validate(PpacIosRateLimitStrategy validator, ApiTokenData apiTokenData) {
    this.validationCommand.accept(validator, apiTokenData);
  }

  /**
   * Calls the save method provided Repository with the provided API Token String as the input, corresponding to the
   * current scenario. .
   *
   * @param repository {@link PpacIosScenarioRepository} which stores the provided API Token.
   * @param apiTokenData   {@link String} of the API Token Key that is to be saved.
   */
  public void save(PpacIosScenarioRepository repository, ApiTokenData apiTokenData) {
    this.insertCommand.accept(repository, apiTokenData);
  }

  /**
   * Update an existing apitoken.
   *
   * @param ppacIosScenarioRepository the repository to use.
   * @param apiTokenData                  the apitoken to update.
   */
  public void update(PpacIosScenarioRepository ppacIosScenarioRepository, ApiTokenData apiTokenData) {
    this.updateCommand.accept(ppacIosScenarioRepository, apiTokenData);
  }

  public void validateIntegrity(PpacAndroidIntegrityValidator integrityValidator,
      AttestationStatement attestationStatement) {
    this.integrityValidator.accept(integrityValidator, attestationStatement);
  }
}
