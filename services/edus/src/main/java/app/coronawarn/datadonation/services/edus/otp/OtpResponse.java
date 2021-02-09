package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpState;

public class OtpResponse {

  private String otp;
  private OtpState state;

  public OtpResponse(String otp, OtpState state) {
    this.otp = otp;
    this.state = state;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }

  public OtpState getState() {
    return state;
  }

  public void setState(OtpState state) {
    this.state = state;
  }
}
