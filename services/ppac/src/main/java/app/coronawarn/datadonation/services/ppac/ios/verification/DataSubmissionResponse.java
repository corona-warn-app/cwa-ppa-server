package app.coronawarn.datadonation.services.ppac.ios.verification;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSubmissionResponse {

  @JsonProperty("errorState")
  private PpacIosErrorStates ppacIosErrorStates;

  public PpacIosErrorStates getErrorState() {
    return ppacIosErrorStates;
  }

  public void setErrorState(PpacIosErrorStates ppacIosErrorStates) {
    this.ppacIosErrorStates = ppacIosErrorStates;
  }

  /**
   * Simple helper method to create a DataSubmissionResponse with the provided ErrorState {@link PpacIosErrorStates}.
   *
   * @param state the provided ErrorState.
   * @return a new instance of DataSubmissionResponse.
   */
  public static DataSubmissionResponse of(PpacIosErrorStates state) {
    DataSubmissionResponse dataSubmissionResponse = new DataSubmissionResponse();
    dataSubmissionResponse.setErrorState(state);
    return dataSubmissionResponse;
  }
}
