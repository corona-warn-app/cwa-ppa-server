package app.coronawarn.datadonation.services.ppac.commons;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.SrsOtpService;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.async.DeferredResult;

public abstract class AbstractController {

  @Autowired
  protected PpacConfiguration ppacConfiguration;

  @Autowired
  protected PpaDataService ppaDataService;

  @Autowired
  protected OtpService otpService;

  @Autowired
  protected ElsOtpService elsOtpService;

  @Autowired
  protected SrsOtpService srsOtpService;

  @Autowired
  protected SecurityLogger securityLogger;

  protected final AbstractDelayManager delayManager;

  /**
   * Used in {@link #deferredResult(ZonedDateTime, StopWatch)}, to delay all requests to an equal response time.
   */
  private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

  protected AbstractController(final AbstractDelayManager delayManager) {
    this.delayManager = delayManager;
  }

  /**
   * Creates new {@link OtpCreationResponse} with given expirationTime, uses it as body for the {@link ResponseEntity}.
   * {@link #delayManager} will be updated with {@link StopWatch#getTotalTimeMillis()}, if that's longer than the total
   * time, a new jittered fake delay is calculated and the difference to the actual time is used as delay.
   *
   * @param expirationTime used as input for the {@link OtpCreationResponse}.
   * @param stopWatch      will be stopped and used to determine the execution time.
   * @return {@link DeferredResult}, which will be 'resulted' when the calculated time is reached.
   */
  protected DeferredResult<ResponseEntity<OtpCreationResponse>> deferredResult(final ZonedDateTime expirationTime,
      final StopWatch stopWatch) {
    return deferredResult(expirationTime, stopWatch, HttpStatus.OK);
  }

  /**
   * Allows to set other HttpStatus as response code.
   * 
   * @param expirationTime used as input for the {@link OtpCreationResponse}.
   * @param stopWatch      will be stopped and used to determine the execution time.
   * @param responseCode   normally HttpStatus.OK, but for data donations it's NO_CONTENT
   * @return {@link DeferredResult}, which will be 'resulted' when the calculated time is reached.
   */
  protected DeferredResult<ResponseEntity<OtpCreationResponse>> deferredResult(final ZonedDateTime expirationTime,
      final StopWatch stopWatch, final HttpStatus responseCode) {
    final DeferredResult<ResponseEntity<OtpCreationResponse>> result = new DeferredResult<>();
    final ResponseEntity<OtpCreationResponse> response = ResponseEntity.status(responseCode)
        .body(new OtpCreationResponse(expirationTime));
    stopWatch.stop();
    final long totalTimeMillis = stopWatch.getTotalTimeMillis();
    long delay = 0;
    if (delayManager.updateFakeRequestDelay(totalTimeMillis) > totalTimeMillis
        && (delay = Math.max(0, delayManager.getJitteredFakeDelay() - totalTimeMillis)) > 0) {
      scheduledExecutor.schedule(() -> result.setResult(response), delay, MILLISECONDS);
    } else {
      result.setResult(response);
    }
    return result;
  }

  /**
   * Creates a new {@link StopWatch} and starts it.
   * 
   * @return new started {@link StopWatch}.
   */
  protected StopWatch start() {
    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    return stopWatch;
  }
}
