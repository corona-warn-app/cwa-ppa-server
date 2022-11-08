package app.coronawarn.datadonation.services.srs.otp;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.service.OtpState;
import org.junit.jupiter.api.Test;

class SrsOtpRedemtionResponseTest {

  @Test
  void modifySrsRedemptionResponseUsingSetters() {
    final String expValue = "eb1f6e7d-7824-421e-9810-ec7a706f9372";
    final SrsOtpRedemptionResponse otpRedemptionResponse = new SrsOtpRedemptionResponse(
        "eb1f6e7d-7824-421e-9810-ec7a706f9370", OtpState.VALID, true);

    otpRedemptionResponse.setOtp(expValue);
    otpRedemptionResponse.setState(OtpState.REDEEMED);
    otpRedemptionResponse.setStrongClientIntegrityCheck(false);

    assertThat(otpRedemptionResponse.getOtp()).isEqualTo(expValue);
    assertThat(otpRedemptionResponse.getState()).isEqualTo(OtpState.REDEEMED);
    assertThat(otpRedemptionResponse.isStrongClientIntegrityCheck()).isFalse();
  }
}
