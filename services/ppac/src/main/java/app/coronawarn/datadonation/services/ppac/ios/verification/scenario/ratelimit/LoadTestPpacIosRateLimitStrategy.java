package app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loadtest")
public class LoadTestPpacIosRateLimitStrategy implements PpacIosRateLimitStrategy {

  /**
   * Check Rate Limit for EDUS Scenario. ApiToken in a EDUS Scenario can only be used once a month.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForEdus(ApiToken apiToken) {

  }

  /**
   * Check Rate Limit for PPA Scenario. ApiToken in a PPA Scenario can only be used once a day.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  public void validateForPpa(ApiToken apiToken) {

  }

  /**
   * Check Rate Limit for ELS Scenario. ApiToken in a PPA Scenario can only when an error occurs.
   *
   * @param apiToken the ApiToken that needs to be validated.
   */
  @Override
  public void validateForEls(ApiToken apiToken) {

  }

}
