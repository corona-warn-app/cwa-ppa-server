package app.coronawarn.datadonation.services.edus.otp;

public class OtpRedemptionIndicator {

  private boolean success;
  private OtpState state;

  public OtpState getState() {
    return state;
  }

  public boolean isSuccess() {
    return this.state.equals(OtpState.REDEEMED) && this.success;
  }

  public OtpRedemptionIndicator(boolean success, OtpState state) {
    this.success = success;
    this.state = state;
  }

}
