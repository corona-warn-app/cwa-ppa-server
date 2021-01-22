package app.coronawarn.datadonation.services.edus.otp;

import javax.validation.constraints.Size;

public class OtpRequest {

  @Size(min = 36, max = 36)
  private String otp;

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
