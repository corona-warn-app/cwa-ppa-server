package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(UrlConstants.ANDROID)
@Validated
public class AndroidController {

  private static final Logger logger = LoggerFactory.getLogger(AndroidController.class);

  private DeviceAttestationVerifier attestationVerifier;

  AndroidController(DeviceAttestationVerifier attestationVerifier) {
    this.attestationVerifier = attestationVerifier;
  }

  /**
   * Handles diagnosis key submission requests.
   *
   * @param ppaDataRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = UrlConstants.DATA)
  public DeferredResult<ResponseEntity<Void>> submitData(
      @RequestBody PPADataRequestAndroid ppaDataRequest) {

    attestationVerifier.validate(ppaDataRequest.getAuthentication(),
        NonceCalculator.of(ppaDataRequest.getPayload()));

    return buildRealDeferredResult(ppaDataRequest);
  }

  private DeferredResult<ResponseEntity<Void>> buildRealDeferredResult(
      PPADataRequestAndroid ppaDataRequest) {
    DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();
    return deferredResult;
  }
}
