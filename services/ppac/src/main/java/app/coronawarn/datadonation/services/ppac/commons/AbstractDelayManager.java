package app.coronawarn.datadonation.services.ppac.commons;

import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 * {@link AbstractDelayManager} instances manage the response delay in the processing of fake (or "dummy") requests.
 */
public abstract class AbstractDelayManager {

  private final long movingAverageSampleSize;

  private long fakeDelay;

  protected AbstractDelayManager(final PpacConfiguration config) {
    fakeDelay = config.getInitialFakeDelayMilliseconds();
    movingAverageSampleSize = config.getFakeDelayMovingAverageSamples();
  }

  /**
   * Returns the current fake delay in seconds. Used for monitoring.
   *
   * @return fake delay in seconds
   */
  public Double getFakeDelayInSeconds() {
    return fakeDelay / 1000.;
  }

  /**
   * Returns the current fake delay after applying random jitter.
   *
   * @return the fake delay
   */
  public long getJitteredFakeDelay() {
    return new PoissonDistribution(fakeDelay).sample();
  }

  /**
   * Updates the moving average for the request duration with the specified value.
   *
   * @param realRequestDuration the request duration
   */
  public long updateFakeRequestDelay(final long realRequestDuration) {
    fakeDelay = fakeDelay + (realRequestDuration - fakeDelay) / movingAverageSampleSize;
    return fakeDelay;
  }
}
