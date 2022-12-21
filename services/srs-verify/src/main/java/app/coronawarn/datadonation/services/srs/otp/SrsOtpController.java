package app.coronawarn.datadonation.services.srs.otp;

import static app.coronawarn.datadonation.common.config.UrlConstants.SRS;
import static app.coronawarn.datadonation.common.config.UrlConstants.SRS_VERIFY;
import static app.coronawarn.datadonation.common.persistence.service.OtpState.REDEEMED;
import static app.coronawarn.datadonation.common.persistence.service.OtpState.VALID;
import static org.springframework.http.ResponseEntity.ok;

import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import app.coronawarn.datadonation.common.persistence.service.OtpState;
import app.coronawarn.datadonation.common.persistence.service.SrsOtpService;
import io.micrometer.core.annotation.Timed;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SRS_VERIFY)
@Validated
@ControllerAdvice
public class SrsOtpController {

  /**
   * The route to the Event-driven User Surveys endpoint (version agnostic).
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(SrsOtpController.class);

  private final SrsOtpService srsOtpService;

  public SrsOtpController(final SrsOtpService srsOtpService) {
    this.srsOtpService = srsOtpService;
  }

  /**
   * Handling of SRS-OTP-Redemption.
   *
   * @param srsOtpRedemptionRequest Request that contains the SRS-OTP that shall be redeemed.
   * @return Response that contains the redeemed SRS-OTP.
   */
  @PostMapping(value = SRS)
  @Timed(description = "Time spent handling SRS-OTP redemption.")
  public ResponseEntity<SrsOtpRedemptionResponse> redeemSrsOtp(
      @Valid @RequestBody final SrsOtpRedemptionRequest srsOtpRedemptionRequest) {
    final SrsOneTimePassword otp = srsOtpService.getOtp(srsOtpRedemptionRequest.getOtp());
    final boolean wasRedeemed = REDEEMED.equals(srsOtpService.getOtpStatus(otp));

    OtpState otpState = srsOtpService.redeemOtp(otp);

    if (REDEEMED.equals(otpState) && !wasRedeemed) {
      otpState = VALID;
      LOGGER.info("SRS-OTP redeemed successfully.");
    } else {
      LOGGER.warn("SRS-OTP could not be redeemed. State was already: {}", otpState);
    }

    return ok(new SrsOtpRedemptionResponse(srsOtpRedemptionRequest.getOtp(), otpState));
  }
}
