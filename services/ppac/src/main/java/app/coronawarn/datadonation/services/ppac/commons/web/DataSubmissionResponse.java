package app.coronawarn.datadonation.services.ppac.commons.web;

import app.coronawarn.datadonation.services.ppac.logging.PpacErrorCode;

public class DataSubmissionResponse {

  private PpacErrorCode errorState;

  public PpacErrorCode getErrorState() {
    return errorState;
  }

  /**
   * Simple helper method to create a DataSubmissionResponse with the provided ErrorCode {@link PpacErrorCode}.
   *
   * @param errorCode the provided ErrorCode.
   * @return a new instance of DataSubmissionResponse.
   */
  public static DataSubmissionResponse of(PpacErrorCode errorCode) {
    DataSubmissionResponse dataSubmissionResponse = new DataSubmissionResponse();
    dataSubmissionResponse.errorState = errorCode;
    return dataSubmissionResponse;
  }
}
