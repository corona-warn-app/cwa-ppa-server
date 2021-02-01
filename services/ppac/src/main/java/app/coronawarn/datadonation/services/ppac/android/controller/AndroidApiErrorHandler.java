package app.coronawarn.datadonation.services.ppac.android.controller;

import static app.coronawarn.datadonation.services.ppac.domain.DataSubmissionResponse.of;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCalculationError;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.NonceCouldNotBeVerified;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.SaltNotValidAnymore;
import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;
import app.coronawarn.datadonation.services.ppac.logging.PpacLogger;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AndroidApiErrorHandler extends ResponseEntityExceptionHandler {

  private PpacLogger ppacLogger;

  public AndroidApiErrorHandler(PpacLogger ppacLogger) {
    this.ppacLogger = ppacLogger;
  }

  private static final Logger logger = LoggerFactory.getLogger(AndroidApiErrorHandler.class);

  /**
   * Mapping of business logic exceptions to codes delivered to the client.
   */
  private static final Map<Class<? extends RuntimeException>, PpacErrorState> ERROR_STATES =
      Map.of(FailedJwsParsing.class, PpacErrorState.JWS_SIGNATURE_VERIFICATION_FAILED,
          FailedSignatureVerification.class, PpacErrorState.JWS_SIGNATURE_VERIFICATION_FAILED,
          SaltNotValidAnymore.class, PpacErrorState.JWS_SIGNATURE_VERIFICATION_FAILED,
          FailedAttestationTimestampValidation.class, PpacErrorState.ATTESTATION_EXPIRED,
          NonceCouldNotBeVerified.class, PpacErrorState.NONCE_MISMATCH,
          ApkPackageNameNotAllowed.class, PpacErrorState.APK_PACKAGE_NAME_MISMATCH,
          ApkCertificateDigestsNotAllowed.class, PpacErrorState.APK_CERTIFICATE_MISMATCH);

  @ExceptionHandler(value = {FailedJwsParsing.class, FailedSignatureVerification.class})
  protected ResponseEntity<Object> handleAuthenticationErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.getLogger().accept(ppacLogger, runtimeException);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(of(errorCode));
  }

  @ExceptionHandler(value = {FailedAttestationTimestampValidation.class,
      FailedAttestationHostnameValidation.class, ApkPackageNameNotAllowed.class,
      ApkCertificateDigestsNotAllowed.class, NonceCouldNotBeVerified.class,
      SaltNotValidAnymore.class})
  protected ResponseEntity<Object> handleForbiddenErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.getLogger().accept(ppacLogger, runtimeException);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(of(errorCode));
  }

  @ExceptionHandler(value = MissingMandatoryAuthenticationFields.class)
  protected ResponseEntity<Object> handleMissingInformationOrBadRequests(
      RuntimeException runtimeException, WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.getLogger().accept(ppacLogger, runtimeException);
    return ResponseEntity.badRequest().body(of(errorCode));
  }

  @ExceptionHandler(value = NonceCalculationError.class)
  protected ResponseEntity<Object> handleInternalServerErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    final PpacErrorState errorCode = getErrorCode(runtimeException);
    errorCode.getLogger().accept(ppacLogger, runtimeException);
    return new ResponseEntity<>(PpacErrorState.INTERNAL_SERVER_ERROR, new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private PpacErrorState getErrorCode(RuntimeException runtimeException) {
    return ERROR_STATES.getOrDefault(runtimeException.getClass(), PpacErrorState.UNKNOWN);
  }
}
