package app.coronawarn.datadonation.services.edus.otp;

public class OtpValidationResponse {

  private String otp;
  private Boolean valid;

  public OtpValidationResponse(String otp, Boolean valid) {
    this.otp = otp;
    this.valid = valid;
  }

  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}
