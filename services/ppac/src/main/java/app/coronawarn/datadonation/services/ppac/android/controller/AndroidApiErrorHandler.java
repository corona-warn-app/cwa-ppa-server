package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.commons.web.DataSubmissionResponse.of;
import static java.util.Map.*;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicIntegrityIsRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.CtsProfileMatchRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AndroidApiErrorHandler extends ResponseEntityExceptionHandler {

  private SecurityLogger securityLogger;

  public AndroidApiErrorHandler(SecurityLogger securityLogger) {
    this.securityLogger = securityLogger;
  }

  /**
   * Mapping of business logic exceptions to codes delivered to the client.
   */
  private static final Map<Class<? extends RuntimeException>, PpacErrorState> ERROR_STATES =
      ofEntries(entry(FailedJwsParsing.class, PpacErrorState.JWS_SIGNATURE_VERIFICATION_FAILED),
          entry(FailedSignatureVerification.class, PpacErrorState.JWS_SIGNATURE_VERIFICATION_FAILED),
          entry(SaltNotValidAnymore.class, PpacErrorState.JWS_SIGNATURE_VERIFICATION_FAILED),
          entry(FailedAttestationTimestampValidation.class, PpacErrorState.ATTESTATION_EXPIRED),
          entry(NonceCouldNotBeVerified.class, PpacErrorState.NONCE_MISMATCH),
          entry(ApkPackageNameNotAllowed.class, PpacErrorState.APK_PACKAGE_NAME_MISMATCH),
          entry(ApkCertificateDigestsNotAllowed.class, PpacErrorState.APK_CERTIFICATE_MISMATCH),
          entry(BasicIntegrityIsRequired.class, PpacErrorState.BASIC_INTEGRITY_REQUIRED), 
          entry(CtsProfileMatchRequired.class, PpacErrorState.CTS_PROFILE_MATCH_REQUIRED), 
          entry(BasicEvaluationTypeNotPresent.class, PpacErrorState.EVALUATION_TYPE_BASIC_REQUIRED),
          entry(HardwareBackedEvaluationTypeNotPresent.class, PpacErrorState.EVALUATION_TYPE_HARDWARE_BACKED_REQUIRED));
      
  @ExceptionHandler(value = {FailedJwsParsing.class, FailedSignatureVerification.class})
  protected ResponseEntity<Object> handleAuthenticationErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(of(errorCode));
  }

  @ExceptionHandler(value = {FailedAttestationTimestampValidation.class,
      FailedAttestationHostnameValidation.class, ApkPackageNameNotAllowed.class,
      ApkCertificateDigestsNotAllowed.class, NonceCouldNotBeVerified.class,
      SaltNotValidAnymore.class, BasicIntegrityIsRequired.class, CtsProfileMatchRequired.class,
      BasicEvaluationTypeNotPresent.class, HardwareBackedEvaluationTypeNotPresent.class})
  protected ResponseEntity<Object> handleForbiddenErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(of(errorCode));
  }

  @ExceptionHandler(value = MissingMandatoryAuthenticationFields.class)
  protected ResponseEntity<Object> handleMissingInformationOrBadRequests(
      RuntimeException runtimeException, WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return ResponseEntity.badRequest().body(of(errorCode));
  }

  @ExceptionHandler(value = NonceCalculationError.class)
  protected ResponseEntity<Object> handleInternalServerErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.secureLog(securityLogger, runtimeException);
    return new ResponseEntity<>(PpacErrorState.INTERNAL_SERVER_ERROR, new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private PpacErrorState getErrorCode(RuntimeException runtimeException) {
    return ERROR_STATES.getOrDefault(runtimeException.getClass(), PpacErrorState.UNKNOWN);
  }
}