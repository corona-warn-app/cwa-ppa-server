package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import java.util.function.BiConsumer;

public enum PpacIosScenario {

  EDUS(PpacIosScenarioValidator::validateForEdus, PpacIosScenarioRepository::saveForEdus),
  PPA(PpacIosScenarioValidator::validateForPpa, PpacIosScenarioRepository::saveForPpa);

  private final BiConsumer<PpacIosScenarioValidator, ApiToken> validator;
  private final BiConsumer<PpacIosScenarioRepository, String> repository;

  PpacIosScenario(BiConsumer<PpacIosScenarioValidator, ApiToken> validator,
      BiConsumer<PpacIosScenarioRepository, String> repository) {
    this.validator = validator;
    this.repository = repository;
  }

  /**
   * TODO.
   * @param validator TODO.
   * @param apiToken TODO.
   */
  public void validate(PpacIosScenarioValidator validator, ApiToken apiToken) {
    this.validator.accept(validator, apiToken);
  }

  /**
   * TODO.
   * @param repository TODO.
   * @param apiToken TODO.
   */
  public void save(PpacIosScenarioRepository repository, String apiToken) {
    this.repository.accept(repository, apiToken);
  }
}
