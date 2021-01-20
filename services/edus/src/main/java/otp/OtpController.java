package otp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version/v1")
@Validated
public class OtpController {
  /**
   * The route to the submission endpoint (version agnostic).
   */
  public static final String EDUS_ROUTE = "/edus/data";
  private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

  public OtpController() {
  }

  /**
   * Handling of Event-driven User Surveys (EDUS).
   *
   * @param otpRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = EDUS_ROUTE)
  public ResponseEntity<OtpResponse> submitData(@RequestBody OtpRequest otpRequest) {
    return new ResponseEntity<OtpResponse>(new OtpResponse(), HttpStatus.OK);
  }
}
