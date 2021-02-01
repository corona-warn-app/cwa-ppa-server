package app.coronawarn.datadonation.services.ppac.ios.verification;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSubmissionResponse {

  @JsonProperty("errorState")
  private PpacIosErrorState ppacIosErrorState;

  public PpacIosErrorState getPpacIosErrorState() {
    return ppacIosErrorState;
  }

  /**
   * Simple helper method to create a DataSubmissionResponse with the provided ErrorState {@link PpacIosErrorState}.
   *
   * @param state the provided ErrorState.
   * @return a new instance of DataSubmissionResponse.
   */
  public static DataSubmissionResponse of(PpacIosErrorState state) {
    DataSubmissionResponse dataSubmissionResponse = new DataSubmissionResponse();
    dataSubmissionResponse.ppacIosErrorState = state;
    return dataSubmissionResponse;

  }
}
