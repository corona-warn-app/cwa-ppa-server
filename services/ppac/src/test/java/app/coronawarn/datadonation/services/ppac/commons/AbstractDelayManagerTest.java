package app.coronawarn.datadonation.services.ppac.commons;

import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.services.ppac.android.controller.AndroidDelayManager;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.junit.jupiter.api.Test;

class AbstractDelayManagerTest {

  @Test
  final void testUpdateFakeRequestDelay() {
    final AbstractDelayManager delayManager = new AndroidDelayManager(new PpacConfiguration() {
      @Override
      public long getFakeDelayMovingAverageSamples() {
        return 5L;
      }

      @Override
      public long getInitialFakeDelayMilliseconds() {
        return 100L;
      }
    });

    assertEquals(0.1, delayManager.getFakeDelayInSeconds()); // 100 : 1000 = 0,1
    delayManager.updateFakeRequestDelay(200); // 100 + (200 - 100) : 5 = 120
    assertEquals(0.12, delayManager.getFakeDelayInSeconds()); // 120 : 1000 = 0,12
  }
}
