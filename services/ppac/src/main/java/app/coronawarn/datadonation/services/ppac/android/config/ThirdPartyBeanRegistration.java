package app.coronawarn.datadonation.services.ppac.android.config;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declare objects from third party libraries which are not component scanned as Spring beans.
 */
@Configuration
public class ThirdPartyBeanRegistration {

  @Bean
  public DefaultHostnameVerifier hostnameVerifier() {
    return new DefaultHostnameVerifier();
  }
}
