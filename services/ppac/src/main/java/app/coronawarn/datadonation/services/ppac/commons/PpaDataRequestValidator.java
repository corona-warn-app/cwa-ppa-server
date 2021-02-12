package app.coronawarn.datadonation.services.ppac.commons;


public abstract class PpaDataRequestValidator<T> {

  /**
   * Based on the provided configuration, perform a validation on the given PPA related payload.
   */
  public abstract void validate(T payload, Integer maxExposureWindowsToRejectSubmission);

}
