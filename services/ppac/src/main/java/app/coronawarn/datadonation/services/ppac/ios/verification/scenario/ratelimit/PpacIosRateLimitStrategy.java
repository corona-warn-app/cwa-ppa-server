package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;

public interface PpacIosRateLimitStrategy {

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  void validateForEdus(ApiTokenData apiTokenData);

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once a day.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  void validateForPpa(ApiTokenData apiTokenData);

  /**
   * Check Rate Limit for SRS Scenario. ApiToken in a SRS Scenario can only be used once a day.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  void validateForSrs(ApiTokenData apiTokenData);

}
