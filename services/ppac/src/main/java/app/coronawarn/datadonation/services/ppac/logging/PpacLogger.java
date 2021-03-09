package app.coronawarn.datadonation.services.ppac.logging;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PpacLogger implements SecurityLogger {

  static final Logger logger = LoggerFactory.getLogger(PpacLogger.class);

  @Override
  public void error(final Exception e) {
    logger.error(e.getMessage(), e);
  }

  @Override
  public void securityWarn(final Exception e) {
    logger.warn(SECURITY, e.getMessage());
  }

  void success(final String os, final String endpoint) {
    logger.info("Successful {} ({}) verification", os, endpoint);
  }

  @Override
  public void successAndroid(final String endpoint) {
    success("Android", endpoint);
  }

  @Override
  public void successIos(final String endpoint) {
    success("iOS", endpoint);
  }
}
