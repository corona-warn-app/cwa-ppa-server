package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.OTP;
import static app.coronawarn.datadonation.common.config.UrlConstants.SURVEY;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SURVEY)
@Profile("test-otp")
public class GenerateOtpController {

  public static final int VALIDITY_IN_HOURS = 24;
  /**
   * The route to the Event-driven User Surveys endpoint for OTP testing.
   */
  private static final Logger logger = LoggerFactory.getLogger(GenerateOtpController.class);

  private OtpService otpService;

  public GenerateOtpController(OtpService otpService) {
    this.otpService = otpService;
  }

  /**
   * Generate a list of OTPs for testing purpose.
   *
   * @return Response that contains a list with new generated OTPs.
   */
  @GetMapping(value = "/smth")
  public ResponseEntity<List<OtpTestGenerationResponse>> getOtp() {
    List<OtpTestGenerationResponse> newGeneratedOtp = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      String password = UUID.randomUUID().toString();
      ZonedDateTime expirationTime = otpService.createOtp(new OneTimePassword(),
          VALIDITY_IN_HOURS);
      OtpTestGenerationResponse otpTestGenerationResponse =
          new OtpTestGenerationResponse(expirationTime, otpService.getOtp(password).getId());
      newGeneratedOtp.add(otpTestGenerationResponse);

    }

    return ResponseEntity.status(HttpStatus.OK).body(newGeneratedOtp);
  }
}
