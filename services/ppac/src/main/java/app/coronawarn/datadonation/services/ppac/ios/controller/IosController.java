package app.coronawarn.datadonation.services.ppac.ios.controller;

import app.coronawarn.datadonation.common.protocols.SubmissionPayloadIos;
import app.coronawarn.datadonation.services.ppac.ios.controller.validation.ValidIosSubmissionPayload;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version/v1")
@Validated
public class IosController {

  /**
   * The route to the submission endpoint (version agnostic).
   */
  public static final String SUBMISSION_ROUTE = "/iOS/data";
  private static final Logger logger = LoggerFactory.getLogger(IosController.class);
  private final PpacProcessor ppacProcessor;

  IosController(PpacProcessor ppacProcessor) {
    this.ppacProcessor = ppacProcessor;
  }

  /**
   * Entry point for validating incoming data submission requests.
   *
   * @param submissionPayloadIos The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = SUBMISSION_ROUTE)
  public ResponseEntity<Object> submitData(
      @ValidIosSubmissionPayload @RequestBody SubmissionPayloadIos submissionPayloadIos) {
    ppacProcessor.validate(submissionPayloadIos);
    return ResponseEntity.noContent().build();
  }


}
