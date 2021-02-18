package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit.PpacIosRateLimitStrategy;
import java.util.function.BiConsumer;

public enum PpacIosScenario {

  EDUS(PpacIosRateLimitStrategy::validateForEdus, PpacIosScenarioRepository::saveForEdus),
  PPA(PpacIosRateLimitStrategy::validateForPpa, PpacIosScenarioRepository::saveForPpa);

  private final BiConsumer<PpacIosRateLimitStrategy, ApiToken> validator;
  private final BiConsumer<PpacIosScenarioRepository, String> repository;

  PpacIosScenario(BiConsumer<PpacIosRateLimitStrategy, ApiToken> validator,
      BiConsumer<PpacIosScenarioRepository, String> repository) {
    this.validator = validator;
    this.repository = repository;
  }

  /**
   * Calls the validate method of the provided Validator with the provided API Token as the input, corresponding to the
   * current scenario.
   *
   * @param validator {@link PpacIosRateLimitStrategy} which validates the provided API Token.
   * @param apiToken  {@link ApiToken} that is to be validated.
   */
  public void validate(PpacIosRateLimitStrategy validator, ApiToken apiToken) {
    this.validator.accept(validator, apiToken);
  }

  /**
   * Calls the save method provided Repository with the provided API Token String as the input, corresponding to the
   * current scenario. .
   *
   * @param repository {@link PpacIosScenarioRepository} which stores the provided API Token.
   * @param apiToken   {@link String} of the API Token Key that is to be saved.
   */
  public void save(PpacIosScenarioRepository repository, String apiToken) {
    this.repository.accept(repository, apiToken);
  }
}
