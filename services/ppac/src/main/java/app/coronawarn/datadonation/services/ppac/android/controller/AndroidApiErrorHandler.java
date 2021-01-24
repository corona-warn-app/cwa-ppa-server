package app.coronawarn.datadonation.services.ppac.android.controller;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkCertificateDigestsNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.ApkPackageNameNotAllowed;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationHostnameValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedAttestationTimestampValidation;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedJwsParsing;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.FailedSignatureVerification;
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

  private static final Logger logger = LoggerFactory.getLogger(AndroidApiErrorHandler.class);

  @ExceptionHandler(value = {FailedJwsParsing.class, FailedSignatureVerification.class})
  protected ResponseEntity<Object> handleAuthenticationErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn(runtimeException.getMessage());
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(),
        HttpStatus.UNAUTHORIZED, webRequest);

  }

  @ExceptionHandler(value = {FailedAttestationTimestampValidation.class,
      FailedAttestationHostnameValidation.class, ApkPackageNameNotAllowed.class,
      ApkCertificateDigestsNotAllowed.class})
  protected ResponseEntity<Object> handleForbiddenErrors(RuntimeException runtimeException,
      WebRequest webRequest) {
    logger.warn(runtimeException.getMessage());
    return handleExceptionInternal(runtimeException, null, new HttpHeaders(), HttpStatus.FORBIDDEN,
        webRequest);
  }
}

