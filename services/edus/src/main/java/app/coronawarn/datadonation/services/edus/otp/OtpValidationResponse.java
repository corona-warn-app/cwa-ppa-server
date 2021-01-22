package app.coronawarn.datadonation.services.edus.otp;

public class OtpValidationResponse {

  private String otp;
  private boolean valid;

  public OtpValidationResponse(String otp, boolean valid) {
    this.otp = otp;
    this.valid = valid;
  }

  public boolean getValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
