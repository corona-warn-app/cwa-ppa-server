package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.services.ppac.commons.AbstractDelayManager;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.springframework.stereotype.Component;

/**
 * {@link IosDelayManager} instances manage the response delay in the processing of fake (or "dummy") requests.
 */
@Component
public class IosDelayManager extends AbstractDelayManager {

  public IosDelayManager(final PpacConfiguration config) {
    super(config);
  }
}
