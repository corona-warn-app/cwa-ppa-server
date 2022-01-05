package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadTestPpacIosRateLimitStrategy implements PpacIosRateLimitStrategy {

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  public void validateForEdus(ApiTokenData apiTokenData) {
    //no implementation needed
  }

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once a day.
   *
   * @param apiTokenData the ApiToken that needs to be validated.
   */
  public void validateForPpa(ApiTokenData apiTokenData) {
    //no implementation needed
  }
}
