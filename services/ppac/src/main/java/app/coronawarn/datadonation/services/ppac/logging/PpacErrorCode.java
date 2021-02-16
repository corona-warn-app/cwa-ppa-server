package app.coronawarn.datadonation.services.ppac.logging;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import java.util.function.BiConsumer;

public enum PpacErrorCode {
  // iOS related error codes
  API_TOKEN_ALREADY_ISSUED(SecurityLogger::warn),
  API_TOKEN_EXPIRED(SecurityLogger::securityWarn),
  API_TOKEN_QUOTA_EXCEEDED(SecurityLogger::securityWarn),
  DEVICE_BLOCKED(SecurityLogger::securityWarn),
  DEVICE_TOKEN_INVALID(SecurityLogger::error),
  DEVICE_TOKEN_REDEEMED(SecurityLogger::securityWarn),
  DEVICE_TOKEN_SYNTAX_ERROR(SecurityLogger::securityWarn),

  // Android related error codes
  APK_CERTIFICATE_MISMATCH(SecurityLogger::securityWarn),
  APK_PACKAGE_NAME_MISMATCH(SecurityLogger::securityWarn),
  ATTESTATION_EXPIRED(SecurityLogger::securityWarn),
  JWS_SIGNATURE_VERIFICATION_FAILED(SecurityLogger::securityWarn),
  NONCE_MISMATCH(SecurityLogger::securityWarn),
  SALT_REDEEMED(SecurityLogger::securityWarn),
  BASIC_INTEGRITY_REQUIRED(SecurityLogger::securityWarn),
  CTS_PROFILE_MATCH_REQUIRED(SecurityLogger::securityWarn),
  EVALUATION_TYPE_BASIC_REQUIRED(SecurityLogger::securityWarn),
  EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED(SecurityLogger::securityWarn),

  // TODO check how these error codes are integrated in code flow
  //BASIC_INTEGRITY_REQUIRED(null),
  //CTS_PROFILE_MATCH_REQUIRED(null),
  //EVALUATION_TYPE_BASIC_REQUIRED(null),
  //EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED(null),

  // COMMONS
  METRICS_DATA_NOT_VALID(SecurityLogger::securityWarn),
  INTERNAL_SERVER_ERROR(SecurityLogger::error),
  UNKNOWN(SecurityLogger::error);

  private final BiConsumer<SecurityLogger, RuntimeException> logInvocation;

  PpacErrorCode(BiConsumer<SecurityLogger, RuntimeException> logInvocation) {
    this.logInvocation = logInvocation;
  }

  public void secureLog(SecurityLogger securityLogger, RuntimeException runtimeException) {
    this.logInvocation.accept(securityLogger, runtimeException);
  }
}
