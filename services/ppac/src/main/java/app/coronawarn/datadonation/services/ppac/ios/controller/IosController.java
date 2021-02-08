package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.common.persistence.service.PpaDataRequestIosConverter;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidPpaDataRequestIosPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;

@RestController
@RequestMapping(IOS)
@Validated
public class IosController {

  private static final Logger logger = LoggerFactory.getLogger(IosController.class);
  private final PpacProcessor ppacProcessor;
  private final PpaDataRequestIosConverter converter;
  private final PpaDataService ppaDataService;

  IosController(PpacProcessor ppacProcessor, PpaDataRequestIosConverter converter, PpaDataService ppaDataService) {
    this.ppacProcessor = ppacProcessor;
    this.converter = converter;
    this.ppaDataService = ppaDataService;
  }

  /**
   * Entry point for validating incoming data submission requests.
   *
   * @param ppaDataRequestIos           The unmarshalled protocol buffers submission payload.
   * @param ignoreApiTokenAlreadyIssued flag to indicate whether the ApiToken should be validated against the last
   *                                    updated time from the per-device Data.
   * @return An empty response body.
   */
  @PostMapping(value = DATA, consumes = "application/x-protobuf")
  public ResponseEntity<Object> submitData(
      @RequestHeader(value = "cwa-ppac-ios-accept-api-token", required = false) boolean ignoreApiTokenAlreadyIssued,
      @ValidPpaDataRequestIosPayload @RequestBody PPADataRequestIOS ppaDataRequestIos) {
    ppacProcessor.validate(ppaDataRequestIos, ignoreApiTokenAlreadyIssued);

    ppaDataService.storeForIos(ppaDataRequestIos);
    return ResponseEntity.noContent().build();
  }
}
