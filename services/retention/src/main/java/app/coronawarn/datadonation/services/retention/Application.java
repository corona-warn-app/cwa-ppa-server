package app.coronawarn.datadonation.services.retention;

import app.coronawarn.datadonation.services.retention.config.RetentionConfiguration;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories(basePackages = "app.coronawarn.datadonation.common.persistence")
@EntityScan(basePackages = "app.coronawarn.datadonation.common.persistence.domain")
@ComponentScan(value = {"app.coronawarn.datadonation.common.persistence",
  "app.coronawarn.datadonation.services.retention"},
        excludeFilters = {
          @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
          @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)})
@EnableConfigurationProperties(RetentionConfiguration.class)
public class Application implements EnvironmentAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class);
  }

  /**
   * Terminates this application with exit code 1 (general error).
   */
  public static void killApplication(ApplicationContext appContext) {
    SpringApplication.exit(appContext);
    LOGGER.error("Application terminated abnormally.");
    System.exit(1);
  }

  @Override
  public void setEnvironment(Environment environment) {
    List<String> profiles = Arrays.asList(environment.getActiveProfiles());
    LOGGER.info("Enabled named groups: {}", System.getProperty("jdk.tls.namedGroups"));
    if (profiles.contains("disable-ssl-client-postgres")) {
      LOGGER.warn("The retention service is started with postgres connection TLS disabled. "
          + "This should never be used in PRODUCTION!");
    }
  }
}
