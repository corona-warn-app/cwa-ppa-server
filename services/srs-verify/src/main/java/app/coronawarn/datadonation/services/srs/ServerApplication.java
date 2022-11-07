package app.coronawarn.datadonation.services.srs;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
@EnableJdbcRepositories(basePackages = "app.coronawarn.datadonation.common.persistence")
@EntityScan(basePackages = "app.coronawarn.datadonation.common.persistence")
@ComponentScan({ "app.coronawarn.datadonation.common.persistence", "app.coronawarn.datadonation.services.srs" })
public class ServerApplication implements EnvironmentAware {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerApplication.class);

  public static void main(final String[] args) {
    SpringApplication.run(ServerApplication.class);
  }

  @Override
  public void setEnvironment(final Environment environment) {
    final List<String> profiles = Arrays.asList(environment.getActiveProfiles());

    LOGGER.info("Enabled named groups: {}", System.getProperty("jdk.tls.namedGroups"));
    if (profiles.contains("disable-ssl-client-postgres")) {
      LOGGER.warn("The SRS-verify service is started with postgres connection TLS disabled. "
          + "This should never be used in PRODUCTION!");
    }
  }
}
