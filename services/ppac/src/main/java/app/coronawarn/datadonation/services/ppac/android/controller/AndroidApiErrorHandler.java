package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse.of;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.APK_CERTIFICATE_MISMATCH;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.APK_PACKAGE_NAME_MISMATCH;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.ATTESTATION_EXPIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.BASIC_INTEGRITY_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.CTS_PROFILE_MATCH_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.DEVICE_QUOTA_EXCEEDED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.EVALUATION_TYPE_BASIC_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.FAILED_ATTESTATION_HOSTNAME_VALIDATION;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.JWS_SIGNATURE_VERIFICATION_FAILED;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.MISSING_MANDATORY_AUTHENTICATION_FIELDS;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.NONCE_MISMATCH;
import static app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode.SALT_REDEEMED;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicIntegrityIsRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.CtsProfileMatchRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.DeviceQuotaExceeded;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AndroidApiErrorHandler extends ResponseEntityExceptionHandler {

  private SecurityLogger securityLogger;

  public AndroidApiErrorHandler(SecurityLogger securityLogger) {
    this.securityLogger = securityLogger;
  }

  /**
   * Mapping of business logic exceptions to codes delivered to the client.
   */
  private static final Map<Class<? extends RuntimeException>, PpacErrorCode> ERROR_CODES = ofEntries(
      entry(FailedJwsParsing.class, JWS_SIGNATURE_VERIFICATION_FAILED),
      entry(FailedSignatureVerification.class, JWS_SIGNATURE_VERIFICATION_FAILED),
      entry(FailedAttestationHostnameValidation.class, FAILED_ATTESTATION_HOSTNAME_VALIDATION),
      entry(SaltNotValidAnymore.class, SALT_REDEEMED),
      entry(FailedAttestationTimestampValidation.class, ATTESTATION_EXPIRED),
      entry(NonceCouldNotBeVerified.class, NONCE_MISMATCH),
      entry(ApkPackageNameNotAllowed.class, APK_PACKAGE_NAME_MISMATCH),
      entry(ApkCertificateDigestsNotAllowed.class, APK_CERTIFICATE_MISMATCH),
      entry(BasicIntegrityIsRequired.class, BASIC_INTEGRITY_REQUIRED),
      entry(CtsProfileMatchRequired.class, CTS_PROFILE_MATCH_REQUIRED),
      entry(BasicEvaluationTypeNotPresent.class, EVALUATION_TYPE_BASIC_REQUIRED),
      entry(MissingMandatoryAuthenticationFields.class, MISSING_MANDATORY_AUTHENTICATION_FIELDS),
      entry(DeviceQuotaExceeded.class, DEVICE_QUOTA_EXCEEDED),
      entry(HardwareBackedEvaluationTypeNotPresent.class, EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED));

  @ExceptionHandler(value = { FailedJwsParsing.class, FailedSignatureVerification.class })
  protected ResponseEntity<Object> handleAuthenticationErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorCode errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(of(errorCode));
  }

  @ExceptionHandler(value = { FailedAttestationTimestampValidation.class, FailedAttestationHostnameValidation.class,
      ApkPackageNameNotAllowed.class, ApkCertificateDigestsNotAllowed.class, NonceCouldNotBeVerified.class,
      SaltNotValidAnymore.class, BasicIntegrityIsRequired.class, CtsProfileMatchRequired.class,
      BasicEvaluationTypeNotPresent.class, HardwareBackedEvaluationTypeNotPresent.class, DeviceQuotaExceeded.class })
  protected ResponseEntity<Object> handleForbiddenErrors(RuntimeException runtimeException, WebRequest webRequest) {
    final PpacErrorCode errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(of(errorCode));
  }

  @ExceptionHandler(value = MissingMandatoryAuthenticationFields.class)
  protected ResponseEntity<Object> handleMissingInformationOrBadRequests(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorCode errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return ResponseEntity.badRequest().body(of(errorCode));
  }

  @ExceptionHandler(value = NonceCalculationError.class)
  protected ResponseEntity<Object> handleInternalServerErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorCode errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return new ResponseEntity<>(PpacErrorCode.INTERNAL_SERVER_ERROR, new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private PpacErrorCode getErrorCode(RuntimeException runtimeException) {
    return ERROR_CODES.getOrDefault(runtimeException.getClass(), PpacErrorCode.UNKNOWN);
  }
}