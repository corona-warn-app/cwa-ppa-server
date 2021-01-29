package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.common.config.UrlConstants;
import app.coronawarn.datadonation.common.protocols.SubmissionPayloadIos;
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
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping(UrlConstants.IOS)
@Validated
public class IosController {

  private static final Logger logger = LoggerFactory.getLogger(IosController.class);
  private final DataDonationProcessor dataDonationProcessor;

  IosController(DataDonationProcessor dataDonationProcessor) {
    this.dataDonationProcessor = dataDonationProcessor;
  }

  /**
   * Handles diagnosis key submission requests.
   *
   * @param submissionPayloadIos The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = UrlConstants.DATA)
  public DeferredResult<ResponseEntity<Void>> submitData(
      @ValidIosSubmissionPayload @RequestBody SubmissionPayloadIos submissionPayloadIos) {
    return buildRealDeferredResult(submissionPayloadIos);
  }

  private DeferredResult<ResponseEntity<Void>> buildRealDeferredResult(
      SubmissionPayloadIos submissionPayload) {
    DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();
    dataDonationProcessor.process(submissionPayload);
    deferredResult.setResult(ResponseEntity.ok().build());

    return deferredResult;
  }
}
