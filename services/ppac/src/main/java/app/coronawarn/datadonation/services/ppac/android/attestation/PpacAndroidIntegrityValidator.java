package app.coronawarn.datadonation.services.ppac.android.attestation;

import static java.lang.Boolean.TRUE;

import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement.EvaluationType;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.BasicIntegrityIsRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.CtsProfileMatchRequired;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.HardwareBackedEvaluationTypeNotPresent;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.springframework.stereotype.Component;

@Component
public class PpacAndroidIntegrityValidator {

  private final PpacConfiguration appParameters;

  public PpacAndroidIntegrityValidator(PpacConfiguration appParameters) {
    this.appParameters = appParameters;
  }

  /**
   * Validate integrity for a given AttestationStatement with the configuration for Otp/EDUS.
   *
   * @param attestationStatement the given attestation.
   */
  public void validateIntegrityForEdus(AttestationStatement attestationStatement) {
    if (TRUE.equals(appParameters.getAndroid().getOtp().getRequireBasicIntegrity())
        && !attestationStatement.isBasicIntegrity()) {
      throw new BasicIntegrityIsRequired();
    }
    if (TRUE.equals(appParameters.getAndroid().getOtp().getRequireCtsProfileMatch())
        && !attestationStatement.isCtsProfileMatch()) {
      throw new CtsProfileMatchRequired();
    }
    if (TRUE.equals(appParameters.getAndroid().getOtp().getRequireEvaluationTypeBasic()
        && !attestationStatement.isEvaluationTypeEqualTo(EvaluationType.BASIC))) {
      throw new BasicEvaluationTypeNotPresent();
    }
    if (TRUE.equals(appParameters.getAndroid().getOtp().getRequireEvaluationTypeHardwareBacked()
        && !attestationStatement.isEvaluationTypeEqualTo(EvaluationType.HARDWARE_BACKED))) {
      throw new HardwareBackedEvaluationTypeNotPresent();
    }
  }

  /**
   * Validate integrity for a given AttestationStatement with the configuration for PPA.
   *
   * @param attestationStatement the given attestation.
   */
  public void validateIntegrityForPpa(AttestationStatement attestationStatement) {
    if (TRUE.equals(appParameters.getAndroid().getDat().getRequireBasicIntegrity())
        && !attestationStatement.isBasicIntegrity()) {
      throw new BasicIntegrityIsRequired();
    }
    if (TRUE.equals(appParameters.getAndroid().getDat().getRequireCtsProfileMatch())
        && !attestationStatement.isCtsProfileMatch()) {
      throw new CtsProfileMatchRequired();
    }
    if (TRUE.equals(appParameters.getAndroid().getDat().getRequireEvaluationTypeBasic())
        && !attestationStatement.isEvaluationTypeEqualTo(EvaluationType.BASIC)) {
      throw new BasicEvaluationTypeNotPresent();
    }
    if (TRUE.equals(appParameters.getAndroid().getDat().getRequireEvaluationTypeHardwareBacked())
        && !attestationStatement.isEvaluationTypeEqualTo(EvaluationType.HARDWARE_BACKED)) {
      throw new HardwareBackedEvaluationTypeNotPresent();
    }
  }

  /**
   * Validate integrity for a given AttestationStatement with the configuration for ELS.
   *
   * @param attestationStatement the given attestation.
   */
  public void validateIntegrityForEls(AttestationStatement attestationStatement) {
    if (TRUE.equals(appParameters.getAndroid().getLog().getRequireBasicIntegrity())
        && !attestationStatement.isBasicIntegrity()) {
      throw new BasicIntegrityIsRequired();
    }
    if (TRUE.equals(appParameters.getAndroid().getLog().getRequireCtsProfileMatch())
        && !attestationStatement.isCtsProfileMatch()) {
      throw new CtsProfileMatchRequired();
    }
    if (TRUE.equals(appParameters.getAndroid().getLog().getRequireEvaluationTypeBasic())
        && !attestationStatement.isEvaluationTypeEqualTo(EvaluationType.BASIC)) {
      throw new BasicEvaluationTypeNotPresent();
    }
    if (TRUE.equals(appParameters.getAndroid().getLog().getRequireEvaluationTypeHardwareBacked())
        && !attestationStatement.isEvaluationTypeEqualTo(EvaluationType.HARDWARE_BACKED)) {
      throw new HardwareBackedEvaluationTypeNotPresent();
    }
  }
}
