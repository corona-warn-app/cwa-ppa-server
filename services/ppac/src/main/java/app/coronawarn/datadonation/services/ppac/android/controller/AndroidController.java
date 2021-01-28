package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.protocols.android.PpaDataRequestAndroid.PPADataRequestAndroid;
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
@RequestMapping("/version/v1")
@Validated
public class AndroidController {

  /**
   * The route to the submission endpoint (version agnostic).
   */
  public static final String SUBMISSION_ROUTE = "/android/data";
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
  @PostMapping(value = SUBMISSION_ROUTE)
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
