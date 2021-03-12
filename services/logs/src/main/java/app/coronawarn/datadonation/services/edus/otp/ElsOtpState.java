package app.coronawarn.datadonation.services.edus.otp;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ElsOtpState {
  EXPIRED("expired"),
  REDEEMED("redeemed"),
  VALID("valid");

  private String state;

  ElsOtpState(String state) {
    this.state = state;
  }

  @JsonValue
  final String state() {
    return this.state;
  }

  @Override
  public String toString() {
    return state;
  }
}
