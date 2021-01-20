package app.coronawarn.analytics.services.edus.otp;

import app.coronawarn.analytics.common.persistence.domain.OtpData;
import app.coronawarn.analytics.common.persistence.repository.OtpDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/version/v1")
@Validated
public class OtpController {

  /**
   * The route to the submission endpoint (version agnostic).
   */
  public static final String EDUS_ROUTE = "/edus/data";
  private static final Logger logger = LoggerFactory.getLogger(OtpController.class);

  OtpDataRepository dataRepository;

  public OtpController(OtpDataRepository dataRepository) {
    this.dataRepository = dataRepository;
  }

  /**
   * Handling of Event-driven User Surveys (EDUS).
   *
   * @param otpRequest The unmarshalled protocol buffers submission payload.
   * @return An empty response body.
   */
  @PostMapping(value = EDUS_ROUTE)
  public ResponseEntity<OtpResponse> submitData(@RequestBody OtpRequest otpRequest) {
    Optional<OtpData> otpData = dataRepository.findById(otpRequest.getOtp());
    AtomicBoolean isValid = new AtomicBoolean(false);

    otpData.ifPresent(otp -> {
      if (otp.getExpirationDate().after(new Date())) {
        isValid.set(true);
      }
    });

    return new ResponseEntity<OtpResponse>(new OtpResponse(otpRequest.getOtp(),isValid.get()), HttpStatus.OK);
  }
}
