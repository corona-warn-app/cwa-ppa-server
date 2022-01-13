package app.coronawarn.datadonation.services.ppac.commons;


public interface PpaDataRequestValidator<T> {

  /**
   * Based on the provided configuration, perform a validation on the given PPA related payload.
   */
  void validate(T payload, Integer maxExposureWindowsToRejectSubmission);

}
