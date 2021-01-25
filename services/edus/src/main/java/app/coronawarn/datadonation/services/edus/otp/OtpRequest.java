package app.coronawarn.datadonation.services.edus.otp;

import javax.validation.constraints.Pattern;

public class OtpRequest {

  /**
   * UUID.
   */
  @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
  private String otp;

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
