package app.coronawarn.datadonation.services.edus.otp;

public class OtpResponse {

  private String otp;
  private String state;

  public OtpResponse(String otp, String state) {
    this.otp = otp;
    this.state = state;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }
}
