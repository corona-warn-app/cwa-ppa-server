package app.coronawarn.datadonation.services.retention;

import app.coronawarn.datadonation.services.retention.config.RetentionConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories(basePackages = "app.coronawarn.datadonation.common.persistence")
@EntityScan(basePackages = "app.coronawarn.datadonation.common.persistence.domain")
@ComponentScan({"app.coronawarn.datadonation.common.persistence"})
@EnableConfigurationProperties(RetentionConfiguration.class)
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class);
  }

}
