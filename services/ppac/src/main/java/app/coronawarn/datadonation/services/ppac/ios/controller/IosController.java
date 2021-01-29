package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidPpaDataRequestIosPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.services.ppac.ios.identification.DataDonationProcessor;
import app.coronawarn.datadonation.services.ppac.ios.validation.ValidIosSubmissionPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UrlConstants.IOS)
@Validated
public class IosController {

  private static final Logger logger = LoggerFactory.getLogger(IosController.class);
  private final PpacProcessor ppacProcessor;

  IosController(PpacProcessor ppacProcessor) {
    this.ppacProcessor = ppacProcessor;
  }

  /**
   * Entry point for validating incoming data submission requests.
   *
   * @param ppaDataRequestIos The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = UrlConstants.DATA)
  public ResponseEntity<Object> submitData(
      @ValidPpaDataRequestIosPayload @RequestBody PPADataRequestIOS ppaDataRequestIos) {
    ppacProcessor.validate(ppaDataRequestIos);
    return ResponseEntity.noContent().build();
  }

}
