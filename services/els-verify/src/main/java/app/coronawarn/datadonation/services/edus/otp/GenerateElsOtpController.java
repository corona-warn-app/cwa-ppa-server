package app.coronawarn.datadonation.services.edus.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.LOG;
import static app.coronawarn.datadonation.common.config.UrlConstants.SURVEY;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.ElsOtpService;
import app.coronawarn.datadonation.common.persistence.service.OtpTestGenerationResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SURVEY)
@Profile("generate-els-otp")
public class GenerateElsOtpController {

  /**
   * The route to the Event-driven User Surveys endpoint for ELS OTP testing.
   */
  private static final Logger logger = LoggerFactory.getLogger(GenerateElsOtpController.class);

  @Autowired
  private final ElsOtpService elsOtpService;

  public GenerateElsOtpController(ElsOtpService elsOtpService) {
    this.elsOtpService = elsOtpService;
    logger.warn("DON'T USE PROFILE 'generate-els-otp' IN PRODUCTION ENVIRONMENT!");
  }

  /**
   * Generate a list of ELS for testing purpose.
   *
   * @return Response that contains a list with new generated ELSs.
   */
  @GetMapping(value = LOG + "/{number}/{validity}")
  public ResponseEntity<List<OtpTestGenerationResponse>> generateElsOtp(
      @PathVariable(name = "number") Integer number, @PathVariable("validity") Integer validity) {
    List<OtpTestGenerationResponse> generatedOtps = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      String password = UUID.randomUUID().toString();
      ZonedDateTime expirationTime = elsOtpService.createOtp(new ElsOneTimePassword(password),
          validity);
      OtpTestGenerationResponse otpTestGenerationResponse =
          new OtpTestGenerationResponse(expirationTime, password);
      generatedOtps.add(otpTestGenerationResponse);
    }
    return ResponseEntity.status(HttpStatus.OK).body(generatedOtps);
  }
}
