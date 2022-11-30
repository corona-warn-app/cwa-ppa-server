package app.coronawarn.datadonation.services.ppac.config;

import app.coronawarn.datadonation.services.ppac.android.attestation.ProdSrsRateLimitVerificationStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AndroidTestBeanConfig {

    @Bean
    public ProdSrsRateLimitVerificationStrategy prodSrsRateLimitVerificationStrategy(PpacConfiguration ppacConfiguration) {
        return new ProdSrsRateLimitVerificationStrategy(ppacConfiguration);
    }

    @Bean
    public PpacConfiguration ppacConfiguration() {
        PpacConfiguration ppacConfiguration = new PpacConfiguration();
        ppacConfiguration.setAndroid(new PpacConfiguration.Android());
        ppacConfiguration.getAndroid().setAndroidIdPepper("abcd");
        ppacConfiguration.setSrsTimeBetweenSubmissionsInDays(90);
        return ppacConfiguration;
    }

}
