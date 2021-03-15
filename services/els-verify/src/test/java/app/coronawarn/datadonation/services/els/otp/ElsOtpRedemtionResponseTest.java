package app.coronawarn.datadonation.services.els.otp;

import static org.assertj.core.api.Assertions.assertThat;

import app.coronawarn.datadonation.common.persistence.service.OtpState;
import org.junit.jupiter.api.Test;

class ElsOtpRedemtionResponseTest {

  @Test
  void modifyElsRedemptionResponseUsingSetters() {
    final String expValue = "eb1f6e7d-7824-421e-9810-ec7a706f9372";
    ElsOtpRedemptionResponse elsOtpRedemptionResponse = new ElsOtpRedemptionResponse("eb1f6e7d-7824-421e-9810-ec7a706f9370", OtpState.VALID, true);

    elsOtpRedemptionResponse.setEls(expValue);
    elsOtpRedemptionResponse.setState(OtpState.REDEEMED);
    elsOtpRedemptionResponse.setStrongClientIntegrityCheck(false);

    assertThat(elsOtpRedemptionResponse.getEls()).isEqualTo(expValue);
    assertThat(elsOtpRedemptionResponse.getState()).isEqualTo(OtpState.REDEEMED);
    assertThat(elsOtpRedemptionResponse.isStrongClientIntegrityCheck()).isFalse();
  }
}
