package app.coronawarn.datadonation.services.ppac.ios.verification;

public enum PpacIosErrorState {
  API_TOKEN_ALREADY_ISSUED,
  API_TOKEN_EXPIRED,
  API_TOKEN_QUOTA_EXCEEDED,
  DEVICE_BLOCKED,
  DEVICE_TOKEN_INVALID,
  DEVICE_TOKEN_REDEEMED,
  DEVICE_TOKEN_SYNTAX_ERROR
}
