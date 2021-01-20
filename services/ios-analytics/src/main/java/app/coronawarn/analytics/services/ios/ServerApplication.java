package app.coronawarn.analytics.services.ios;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@SpringBootApplication
@EnableJdbcRepositories(basePackages = "app.coronawarn.analytics.common.persistence")
@EntityScan(basePackages = "app.coronawarn.analytics.common.persistence.domain")
@ComponentScan({"app.coronawarn.analytics.common.persistence", "app.coronawarn.analytics.services.ios"})
@EnableConfigurationProperties
@EnableFeignClients
public class ServerApplication implements EnvironmentAware, DisposableBean {

  private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class);
  }

  /**
   * Manual shutdown hook needed to avoid Log4j shutdown issues (see cwa-server/#589).
   */
  @Override
  public void destroy() {
    LogManager.shutdown();
  }

  @Bean
  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufHttpMessageConverter();
  }

  @Override
  public void setEnvironment(Environment environment) {
    List<String> profiles = Arrays.asList(environment.getActiveProfiles());

    logger.info("Enabled named groups: {}", System.getProperty("jdk.tls.namedGroups"));
    if (profiles.contains("disable-ssl-client-postgres")) {
      logger.warn("The submission service is started with postgres connection TLS disabled. "
          + "This should never be used in PRODUCTION!");
    }
  }
}
