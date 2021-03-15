package app.coronawarn.datadonation.services.els.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpState;

public class ElsOtpRedemptionResponse {

  private String els;
  private OtpState state;
  private boolean strongClientIntegrityCheck;

  /**
   * Constructor.
   *
   * @param els The els one time password .
   * @param state The els OTP state.
   * @param strongClientIntegrityCheck The strongClientIntegrityCheck.
   */
  public ElsOtpRedemptionResponse(String els,
      OtpState state, boolean strongClientIntegrityCheck) {
    this.els = els;
    this.state = state;
    this.strongClientIntegrityCheck = strongClientIntegrityCheck;
  }

  public String getEls() {
    return els;
  }

  public void setEls(String els) {
    this.els = els;
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
