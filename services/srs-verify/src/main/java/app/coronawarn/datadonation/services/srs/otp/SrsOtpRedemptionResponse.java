package app.coronawarn.datadonation.services.srs.otp;

import app.coronawarn.datadonation.common.persistence.service.OtpState;

public class SrsOtpRedemptionResponse {

  private String otp;
  private OtpState state;
  private boolean strongClientIntegrityCheck;

  /**
   * Constructor.
   *
   * @param otp                        The SRS one time password .
   * @param state                      The SRS OTP state.
   * @param strongClientIntegrityCheck The strongClientIntegrityCheck.
   */
  public SrsOtpRedemptionResponse(final String otp, final OtpState state, final boolean strongClientIntegrityCheck) {
    this.otp = otp;
    this.state = state;
    this.strongClientIntegrityCheck = strongClientIntegrityCheck;
  }

  /**
   * Constructor.
   * 
   * @param otp
   * @param state
   */
  public SrsOtpRedemptionResponse(final String otp, final OtpState state) {
    this.otp = otp;
    this.state = state;
    this.strongClientIntegrityCheck = false; // unused - not relevant for this scenario
  }

  public String getOtp() {
    return otp;
  }

  public OtpState getState() {
    return state;
  }

  public boolean isStrongClientIntegrityCheck() {
    return strongClientIntegrityCheck;
  }

  public void setOtp(final String otp) {
    this.otp = otp;
  }

  public void setState(final OtpState state) {
    this.state = state;
  }

  public void setStrongClientIntegrityCheck(final boolean strongClientIntegrityCheck) {
    this.strongClientIntegrityCheck = strongClientIntegrityCheck;
  }
}
