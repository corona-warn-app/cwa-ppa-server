package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.persistence.service.PpaDataStorageRequest;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.services.ppac.android.attestation.DeviceAttestationVerifier;
import app.coronawarn.datadonation.services.ppac.android.attestation.NonceCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UrlConstants.ANDROID)
@Validated
public class AndroidController {

  private DeviceAttestationVerifier attestationVerifier;
  private PpaDataService ppaDataService;
  private PpaDataRequestAndroidConverter converter;

  AndroidController(DeviceAttestationVerifier attestationVerifier, PpaDataService ppaDataService,
      PpaDataRequestAndroidConverter converter) {
    this.attestationVerifier = attestationVerifier;
    this.ppaDataService = ppaDataService;
    this.converter = converter;
  }

  /**
   * Handles diagnosis key submission requests.
   *
   * @param ppaDataRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = UrlConstants.DATA)
  public ResponseEntity<Void> submitData(
      @RequestBody PPADataRequestAndroid ppaDataRequest) {

    attestationVerifier.validate(ppaDataRequest.getAuthentication(),
        NonceCalculator.of(ppaDataRequest.getPayload()));
    final PpaDataStorageRequest dataToStore = this.converter.convertToStorageRequest(ppaDataRequest);
    ppaDataService.store(dataToStore);
    
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
