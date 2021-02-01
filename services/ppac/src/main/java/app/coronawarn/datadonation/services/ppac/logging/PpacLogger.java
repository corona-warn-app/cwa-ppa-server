package app.coronawarn.datadonation.services.ppac.logging;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import org.slf4j.Logger;

public class PpacLogger implements SecurityLogger {

  Logger logger;

  public PpacLogger(Logger logger) {
    this.logger = logger;
  }

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
