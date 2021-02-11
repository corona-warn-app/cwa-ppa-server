package app.coronawarn.datadonation.services.ppac.commons.web;

import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;

public class DataSubmissionResponse {

  private PpacErrorState errorState;

  public PpacErrorState getErrorState() {
    return errorState;
  }

  /**
   * Simple helper method to create a DataSubmissionResponse with the provided ErrorState {@link PpacErrorState}.
   *
   * @param state the provided ErrorState.
   * @return a new instance of DataSubmissionResponse.
   */
  public static DataSubmissionResponse of(PpacErrorState state) {
    DataSubmissionResponse dataSubmissionResponse = new DataSubmissionResponse();
    dataSubmissionResponse.errorState = state;
    return dataSubmissionResponse;
  }
}
