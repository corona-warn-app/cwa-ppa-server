package app.coronawarn.datadonation.services.ppac.config;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestBeanConfig {

  @Bean
  public TestRestTemplate testRestTemplate() {
    return new TestRestTemplate();
  }
}
