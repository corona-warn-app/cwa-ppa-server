package app.coronawarn.datadonation.services.ppac.domain;

import app.coronawarn.datadonation.services.ppac.logging.PpacErrorState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSubmissionResponse {

  @JsonProperty("errorState")
  private PpacErrorState ppacErrorState;

  public PpacErrorState getPpacIosErrorState() {
    return ppacErrorState;
  }

  /**
   * Simple helper method to create a DataSubmissionResponse with the provided ErrorState {@link PpacErrorState}.
   *
   * @param state the provided ErrorState.
   * @return a new instance of DataSubmissionResponse.
   */
  public static DataSubmissionResponse of(PpacErrorState state) {
    DataSubmissionResponse dataSubmissionResponse = new DataSubmissionResponse();
    dataSubmissionResponse.ppacErrorState = state;
    return dataSubmissionResponse;

  }
}
