package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicIntegrityIsRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.CtsProfileMatchRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Dat;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Log;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Otp;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Srs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PpacAndroidIntegrityValidatorTest {

  private PpacAndroidIntegrityValidator androidIntegrityValidator;
  private AttestationStatement attestationStatement;
  private Dat dat;
  private Log log;
  private Otp otp;
  private Srs srs;

  @BeforeEach
  void beforeEach() {
    PpacConfiguration ppacConfiguration = new PpacConfiguration();
    Android android = new Android();
    dat = new Dat();
    android.setDat(dat);
    log = new Log();
    android.setLog(log);
    otp = new Otp();
    srs = new Srs();
    android.setSrs(srs);
    android.setOtp(otp);
    ppacConfiguration.setAndroid(android);
    androidIntegrityValidator = new PpacAndroidIntegrityValidator(ppacConfiguration);
    attestationStatement = new AttestationStatement();
  }

  @Test
  void testDatShouldThrowBasicIntegrityIsRequiredException() {
    dat.setRequireBasicIntegrity(true);
    assertThrows(BasicIntegrityIsRequired.class,
        () -> androidIntegrityValidator.validateIntegrityForPpa(attestationStatement));
  }

  @Test
  void testDatShouldThrowCtsProfileRequiredException() {
    dat.setRequireCtsProfileMatch(true);
    assertThrows(CtsProfileMatchRequired.class,
        () -> androidIntegrityValidator.validateIntegrityForPpa(attestationStatement));
  }

  @Test
  void testDatShouldThrowBasicEvaluationTypeNotPresentException() {
    dat.setRequireEvaluationTypeBasic(true);
    assertThrows(BasicEvaluationTypeNotPresent.class,
        () -> androidIntegrityValidator.validateIntegrityForPpa(attestationStatement));
  }

  @Test
  void testDatShouldThrowHardwareBackedTypeNotPresentException() {
    dat.setRequireEvaluationTypeHardwareBacked(true);
    assertThrows(HardwareBackedEvaluationTypeNotPresent.class,
        () -> androidIntegrityValidator.validateIntegrityForPpa(attestationStatement));
  }

  @Test
  void testNoDatValidationRequired() {
    androidIntegrityValidator.validateIntegrityForPpa(attestationStatement);
  }

  @Test
  void testOtpShouldThrowBasicIntegrityIsRequiredException() {
    otp.setRequireBasicIntegrity(true);
    assertThrows(BasicIntegrityIsRequired.class,
        () -> androidIntegrityValidator.validateIntegrityForEdus(attestationStatement));
  }

  @Test
  void testOtpShouldThrowCtsProfileRequiredException() {
    otp.setRequireCtsProfileMatch(true);
    assertThrows(CtsProfileMatchRequired.class,
        () -> androidIntegrityValidator.validateIntegrityForEdus(attestationStatement));
  }

  @Test
  void testOtpShouldThrowBasicEvaluationTypeNotPresentException() {
    otp.setRequireEvaluationTypeBasic(true);
    assertThrows(BasicEvaluationTypeNotPresent.class,
        () -> androidIntegrityValidator.validateIntegrityForEdus(attestationStatement));
  }

  @Test
  void testOtpShouldThrowHardwareBackedTypeNotPresentException() {
    otp.setRequireEvaluationTypeHardwareBacked(true);
    assertThrows(HardwareBackedEvaluationTypeNotPresent.class,
        () -> androidIntegrityValidator.validateIntegrityForEdus(attestationStatement));
  }

  @Test
  void testNoOtpValidationRequired() {
    androidIntegrityValidator.validateIntegrityForEdus(attestationStatement);
  }

  @Test
  void testLogShouldThrowBasicIntegrityIsRequiredException() {
    log.setRequireBasicIntegrity(true);
    assertThrows(BasicIntegrityIsRequired.class,
        () -> androidIntegrityValidator.validateIntegrityForEls(attestationStatement));
  }

  @Test
  void testLogShouldThrowCtsProfileRequiredException() {
    log.setRequireCtsProfileMatch(true);
    assertThrows(CtsProfileMatchRequired.class,
        () -> androidIntegrityValidator.validateIntegrityForEls(attestationStatement));
  }

  @Test
  void testLogShouldThrowBasicEvaluationTypeNotPresentException() {
    log.setRequireEvaluationTypeBasic(true);
    assertThrows(BasicEvaluationTypeNotPresent.class,
        () -> androidIntegrityValidator.validateIntegrityForEls(attestationStatement));
  }

  @Test
  void testLogShouldThrowHardwareBackedTypeNotPresentException() {
    log.setRequireEvaluationTypeHardwareBacked(true);
    assertThrows(HardwareBackedEvaluationTypeNotPresent.class,
        () -> androidIntegrityValidator.validateIntegrityForEls(attestationStatement));
  }

  @Test
  void testNoElsValidationRequired() {
    androidIntegrityValidator.validateIntegrityForEls(attestationStatement);
  }

  @Test
  void testSrsShouldThrowBasicIntegrityIsRequiredException() {
    srs.setRequireBasicIntegrity(true);
    assertThrows(BasicIntegrityIsRequired.class,
            () -> androidIntegrityValidator.validateIntegrityForSrs(attestationStatement));
  }

  @Test
  void testSrsShouldThrowCtsProfileRequiredException() {
    srs.setRequireCtsProfileMatch(true);
    assertThrows(CtsProfileMatchRequired.class,
            () -> androidIntegrityValidator.validateIntegrityForSrs(attestationStatement));
  }

  @Test
  void testSrsShouldThrowBasicEvaluationTypeNotPresentException() {
    srs.setRequireEvaluationTypeBasic(true);
    assertThrows(BasicEvaluationTypeNotPresent.class,
            () -> androidIntegrityValidator.validateIntegrityForSrs(attestationStatement));
  }

  @Test
  void testSrsShouldThrowHardwareBackedTypeNotPresentException() {
    srs.setRequireEvaluationTypeHardwareBacked(true);
    assertThrows(HardwareBackedEvaluationTypeNotPresent.class,
            () -> androidIntegrityValidator.validateIntegrityForSrs(attestationStatement));
  }
}
