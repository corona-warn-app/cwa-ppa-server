package app.coronawarn.datadonation.services.ppac.config;

import app.coronawarn.datadonation.services.ppac.android.attestation.ProdSrsRateLimitVerificationStrategy;
import app.coronawarn.datadonation.services.ppac.android.attestation.TestSrsRateLimitVerificationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AndroidTestBeanConfig {

  @Bean
  public PpacConfiguration ppacConfiguration() {
    final PpacConfiguration config = new PpacConfiguration();
    config.setAndroid(new PpacConfiguration.Android());
    config.getAndroid().setAndroidIdPepper("abcd");
    config.setSrsTimeBetweenSubmissionsInDays(90);
    return config;
  }

  @Bean
  public ProdSrsRateLimitVerificationStrategy prodStrategy(final PpacConfiguration config) {
    return new ProdSrsRateLimitVerificationStrategy(config);
  }

  @Bean
  public TestSrsRateLimitVerificationStrategy testStrategy(final PpacConfiguration config) {
    return new TestSrsRateLimitVerificationStrategy(config);
  }
}
