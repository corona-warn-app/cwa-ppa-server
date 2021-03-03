package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;

public interface PpacIosRateLimitStrategy {

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  void validateForEdus(ApiToken apiToken);

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once a day.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  void validateForPpa(ApiToken apiToken);

}
