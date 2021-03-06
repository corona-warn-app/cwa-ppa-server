package app.coronawarn.datadonation.services.ppac.ios.client.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PerDeviceDataQueryRequest {

  @JsonProperty("device_token")
  private String deviceToken;

  @JsonProperty("transaction_id")
  private String transactionId;
  private Long timestamp;

  public PerDeviceDataQueryRequest() {
    // empty constructor
  }

  /**
   * Create a new instance of an query request to retrieve per-device data for a given device token.
   *
   * @param deviceToken   the device token as identification.
   * @param transactionId a valid transaction id for this request.
   * @param timestamp     a valid timestamp for this request.
   */
  public PerDeviceDataQueryRequest(String deviceToken, String transactionId, Long timestamp) {
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
