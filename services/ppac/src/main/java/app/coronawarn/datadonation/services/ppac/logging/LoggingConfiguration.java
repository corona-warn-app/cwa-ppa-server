package app.coronawarn.datadonation.services.ppac.logging;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggingConfiguration {

  @Bean
  @Scope(SCOPE_PROTOTYPE)
  public SecurityLogger getLogger(InjectionPoint injectionPoint) {
    Logger logger = LoggerFactory.getLogger(injectionPoint.getDeclaredType());
    return new PpacLogger(logger);
  }
}
