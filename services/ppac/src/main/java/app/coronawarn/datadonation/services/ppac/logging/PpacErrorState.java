package app.coronawarn.datadonation.services.ppac.logging;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.config.SecurityWarnings;
import java.util.function.BiConsumer;

public enum PpacErrorState implements SecurityWarnings {
  API_TOKEN_ALREADY_ISSUED(SecurityLogger::warn),
  API_TOKEN_EXPIRED(SecurityLogger::securityWarn),
  API_TOKEN_QUOTA_EXCEEDED(SecurityLogger::securityWarn),
  DEVICE_BLOCKED(SecurityLogger::securityWarn),
  DEVICE_TOKEN_INVALID(SecurityLogger::securityError),
  DEVICE_TOKEN_REDEEMED(SecurityLogger::securityWarn),
  DEVICE_TOKEN_SYNTAX_ERROR(SecurityLogger::securityWarn),
  INTERNAL_SERVER_ERROR(SecurityLogger::error),
  APK_CERTIFICATE_MISMATCH(SecurityLogger::warn),
  APK_PACKAGE_NAME_MISMATCH(SecurityLogger::warn),
  ATTESTATION_EXPIRED(SecurityLogger::warn),
  JWS_SIGNATURE_VERIFICATION_FAILED(SecurityLogger::warn),
  NONCE_MISMATCH(SecurityLogger::warn),
  SALT_REDEEMED(SecurityLogger::warn),
  UNKNOWN(SecurityLogger::error);

  private final BiConsumer<SecurityLogger, RuntimeException> logger;

  PpacErrorState(BiConsumer<SecurityLogger, RuntimeException> logger) {
    this.logger = logger;
  }

  @Override
  public BiConsumer<SecurityLogger, RuntimeException> getLogger() {
    return logger;
  }
}
