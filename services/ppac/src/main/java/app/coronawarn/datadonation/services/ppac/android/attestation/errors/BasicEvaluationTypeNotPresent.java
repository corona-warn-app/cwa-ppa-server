package app.coronawarn.datadonation.services.ppac.android.attestation.errors;


public class BasicEvaluationTypeNotPresent extends RuntimeException {

  private static final long serialVersionUID = -2513064483236579622L;

  public BasicEvaluationTypeNotPresent() {
    super("Evaluation Type BASIC not found in Android attestation response");
  }
}
