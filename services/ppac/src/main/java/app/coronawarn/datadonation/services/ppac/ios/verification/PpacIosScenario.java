package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit.PpacIosRateLimitStrategy;
import java.util.function.BiConsumer;

public enum PpacIosScenario {

  EDUS(PpacIosRateLimitStrategy::validateForEdus, PpacIosScenarioRepository::saveForEdus,
      PpacIosScenarioRepository::updateForEdus),
  PPA(PpacIosRateLimitStrategy::validateForPpa, PpacIosScenarioRepository::saveForPpa,
      PpacIosScenarioRepository::updateForPpa);

  private final BiConsumer<PpacIosRateLimitStrategy, ApiToken> validationCommand;
  private final BiConsumer<PpacIosScenarioRepository, ApiToken> insertCommand;
  private final BiConsumer<PpacIosScenarioRepository, ApiToken> updateCommand;

  PpacIosScenario(BiConsumer<PpacIosRateLimitStrategy, ApiToken> validationCommand,
      BiConsumer<PpacIosScenarioRepository, ApiToken> insertCommand,
      BiConsumer<PpacIosScenarioRepository, ApiToken> updateCommand) {
    this.validationCommand = validationCommand;
    this.insertCommand = insertCommand;
    this.updateCommand = updateCommand;
  }

  /**
   * Calls the validate method of the provided Validator with the provided API Token as the input, corresponding to the
   * current scenario.
   *
   * @param validator {@link PpacIosRateLimitStrategy} which validates the provided API Token.
   * @param apiToken  {@link ApiToken} that is to be validated.
   */
  public void validate(PpacIosRateLimitStrategy validator, ApiToken apiToken) {
    this.validationCommand.accept(validator, apiToken);
  }

  /**
   * Calls the save method provided Repository with the provided API Token String as the input, corresponding to the
   * current scenario. .
   *
   * @param repository {@link PpacIosScenarioRepository} which stores the provided API Token.
   * @param apiToken   {@link String} of the API Token Key that is to be saved.
   */
  public void save(PpacIosScenarioRepository repository, ApiToken apiToken) {
    this.insertCommand.accept(repository, apiToken);
  }

  /**
   * Update an existing apitoken.
   *
   * @param ppacIosScenarioRepository the repository to use.
   * @param apiToken                  the apitoken to update.
   */
  public void update(PpacIosScenarioRepository ppacIosScenarioRepository, ApiToken apiToken) {
    this.updateCommand.accept(ppacIosScenarioRepository, apiToken);
  }
}
