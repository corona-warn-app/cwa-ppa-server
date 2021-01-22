package app.coronawarn.datadonation.services.edus.otp;

public enum OtpState {
  EXPIRED("expired"),
  REDEEMED("redeemed"),
  VALID("valid");

  private String state;

  OtpState(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return state;
  }
}
