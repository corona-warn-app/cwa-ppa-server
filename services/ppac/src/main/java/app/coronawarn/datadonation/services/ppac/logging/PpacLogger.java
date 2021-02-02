package app.coronawarn.datadonation.services.ppac.logging;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PpacLogger implements SecurityLogger {

  static final Logger logger = LoggerFactory.getLogger(PpacLogger.class);

  public void warn(RuntimeException e) {
    logger.warn(e.getMessage());
  }

  public void error(RuntimeException e) {
    logger.error(e.getMessage(), e.getCause());
  }

  public void securityWarn(RuntimeException e) {
    logger.warn(SECURITY, e.getMessage());
  }

}
