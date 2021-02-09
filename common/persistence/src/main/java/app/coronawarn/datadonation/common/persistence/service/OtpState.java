package app.coronawarn.datadonation.common.persistence.service;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OtpState {
  EXPIRED("expired"),
  REDEEMED("redeemed"),
  VALID("valid");

  private String state;

  OtpState(String state) {
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
