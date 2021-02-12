package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class HardwareBackedEvaluationTypeNotPresent extends RuntimeException {

  private static final long serialVersionUID = -1834500566508031768L;

  public HardwareBackedEvaluationTypeNotPresent() {
    super("Evaluation Type HARDWARE_BACKED not found in Android attestation response");
  }
}
