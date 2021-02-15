package app.coronawarn.datadonation.services.ppac.commons;

public class PpaDataRequestValidationFailed extends RuntimeException {

  private static final long serialVersionUID = -1558962815012631670L;

  public PpaDataRequestValidationFailed(String message) {
    super(message);
  }
}
