package app.coronawarn.datadonation.services.ppac.ios.identification;

import app.coronawarn.datadonation.services.ppac.config.PpacErrorState;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSubmissionResponse {

  @JsonProperty("errorState")
  private PpacErrorState ppacErrorState;

  public PpacErrorState getErrorState() {
    return ppacErrorState;
  }

  public void setErrorState(PpacErrorState ppacErrorState) {
    this.ppacErrorState = ppacErrorState;
  }

  /**
   * Simple helper method to create a DataSubmissionResponse with the provided ErrorState {@link PpacErrorState}.
   *
   * @param state the provided ErrorState.
   * @return a new instance of DataSubmissionResponse.
   */
  public static DataSubmissionResponse of(PpacErrorState state) {
    DataSubmissionResponse dataSubmissionResponse = new DataSubmissionResponse();
    dataSubmissionResponse.setErrorState(state);
    return dataSubmissionResponse;
  }
}
