package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpState;

public class ElsOtpRedemptionResponse {

  private String otp;
  private OtpState state;
  private boolean strongClientIntegrityCheck;

  /**
   * Constructor.
   *
   * @param otp The one time password.
   * @param state The OTP state.
   * @param strongClientIntegrityCheck The strongClientIntegrityCheck.
   */
  public ElsOtpRedemptionResponse(String otp,
      OtpState state, boolean strongClientIntegrityCheck) {
    this.otp = otp;
    this.state = state;
    this.strongClientIntegrityCheck = strongClientIntegrityCheck;
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

  public boolean isStrongClientIntegrityCheck() {
    return strongClientIntegrityCheck;
  }

  public void setStrongClientIntegrityCheck(boolean strongClientIntegrityCheck) {
    this.strongClientIntegrityCheck = strongClientIntegrityCheck;
  }
}
