package app.coronawarn.datadonation.services.ppac.config;

import app.coronawarn.datadonation.services.ppac.android.controller.RequestExecutor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestBeanConfig {

  @Bean
  public RequestExecutor requestExecutor(TestRestTemplate testRestTemplate) {
    return new RequestExecutor(testRestTemplate);
  }
}
