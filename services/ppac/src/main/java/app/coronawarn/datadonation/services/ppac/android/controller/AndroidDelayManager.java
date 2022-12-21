package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.services.ppac.commons.AbstractDelayManager;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.springframework.stereotype.Component;

/**
 * {@link AndroidDelayManager} instances manage the response delay in the processing of fake (or "dummy") requests.
 */
@Component
public class AndroidDelayManager extends AbstractDelayManager {

  public AndroidDelayManager(final PpacConfiguration config) {
    super(config);
  }
}
