package app.coronawarn.datadonation.services.srs.otp;

import jakarta.validation.constraints.Pattern;

public class SrsOtpRedemptionRequest {

  /**
   * UUID.
   */
  @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
  private String otp;

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
