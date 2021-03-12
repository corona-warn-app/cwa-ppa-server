package app.coronawarn.datadonation.services.edus.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpState;

public class ElsOtpRedemptionResponse {

  private String elsOtp;
  private OtpState state;
  private boolean strongClientIntegrityCheck;

  /**
   * Constructor.
   *
   * @param elsOtp The els one time password .
   * @param state The els OTP state.
   * @param strongClientIntegrityCheck The strongClientIntegrityCheck.
   */
  public ElsOtpRedemptionResponse(String elsOtp,
      OtpState state, boolean strongClientIntegrityCheck) {
    this.elsOtp = elsOtp;
    this.state = state;
    this.strongClientIntegrityCheck = strongClientIntegrityCheck;
  }

  public String getElsOtp() {
    return elsOtp;
  }

  public void setElsOtp(String elsOtp) {
    this.elsOtp = elsOtp;
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
