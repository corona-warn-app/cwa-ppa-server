package app.coronawarn.datadonation.services.ppac.ios;

import java.util.Arrays;
import java.util.stream.StreamSupport;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

@Component
public class CallbackFlywayMigrationStrategy implements FlywayMigrationStrategy {

  private static final Logger logger = LoggerFactory.getLogger(CallbackFlywayMigrationStrategy.class);

  @Autowired
  Environment env;

  @Override
  public void migrate(Flyway flyway) {
    logger.info("====== Environment and configuration ======");
    logger.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

    final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
    StreamSupport.stream(sources.spliterator(), false)
        .filter(ps -> ps instanceof EnumerablePropertySource)
        .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
        .flatMap(Arrays::stream)
        .distinct()
        .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
        .forEach(prop -> logger.info("{}: {}", prop, env.getProperty(prop)));
    logger.info("===========================================");
    flyway.migrate();
  }
}
