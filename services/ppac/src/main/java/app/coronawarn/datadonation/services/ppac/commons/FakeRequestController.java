package app.coronawarn.datadonation.services.ppac.commons;

import static app.coronawarn.datadonation.common.config.UrlConstants.ANDROID;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;
import static java.time.ZonedDateTime.now;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.ResponseEntity.ok;

import app.coronawarn.datadonation.common.persistence.service.OtpCreationResponse;
import app.coronawarn.datadonation.services.ppac.android.controller.AndroidDelayManager;
import app.coronawarn.datadonation.services.ppac.ios.controller.IosDelayManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class FakeRequestController {

  public static final String FAKE = "cwa-fake";

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

  private final AndroidDelayManager androidDelayManager;

  private final IosDelayManager iosDelayManager;

  FakeRequestController(final AndroidDelayManager androidDelayManager, final IosDelayManager iosDelayManager) {
    this.androidDelayManager = androidDelayManager;
    this.iosDelayManager = iosDelayManager;
  }

  DeferredResult<ResponseEntity<OtpCreationResponse>> deferredResult(final AbstractDelayManager delayManager) {
    final DeferredResult<ResponseEntity<OtpCreationResponse>> result = new DeferredResult<>();
    final ResponseEntity<OtpCreationResponse> response = ok(new OtpCreationResponse(now()));
    executor.schedule(() -> result.setResult(response), delayManager.getJitteredFakeDelay(), MILLISECONDS);
    return result;
  }

  @PostMapping(value = { ANDROID + SRS }, headers = { FAKE + "!=0" })
  public DeferredResult<ResponseEntity<OtpCreationResponse>> fakeAndroid(@RequestHeader(FAKE) final int fake) {
    return deferredResult(androidDelayManager);
  }

  @PostMapping(value = { IOS + SRS }, headers = { FAKE + "!=0" })
  public DeferredResult<ResponseEntity<OtpCreationResponse>> fakeIos(@RequestHeader(FAKE) final int fake) {
    return deferredResult(iosDelayManager);
  }
}
