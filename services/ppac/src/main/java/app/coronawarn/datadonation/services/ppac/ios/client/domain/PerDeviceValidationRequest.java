package app.coronawarn.datadonation.services.ppac.ios.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PerDeviceValidationRequest {

  @JsonProperty("device_token")
  String deviceToken;
  @JsonProperty("transaction_id")
  String transactionId;
  Long timestamp;

  public PerDeviceValidationRequest() {
    //empty constructor
  }

  /**
   * Create a new instance of an validation request that can be used to verify a device against the Apple Device API.
   *
   * @param deviceToken   the device token used for validation.
   * @param transactionId a valid transaction id for this request.
   * @param timestamp     a valid timestamp for this request.
   */
  public PerDeviceValidationRequest(String deviceToken, String transactionId, Long timestamp) {
    this.deviceToken = deviceToken;
    this.transactionId = transactionId;
    this.timestamp = timestamp;
  }

  public String getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(String deviceToken) {
    this.deviceToken = deviceToken;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }
}
