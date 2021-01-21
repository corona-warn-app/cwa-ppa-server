package app.coronawarn.analytics.services.ios.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceDataUpdateRequest {

  @JsonProperty("device_token")
  private String deviceToken;

  @JsonProperty("transaction_id")
  private String transactionId;
  private Long timestamp;
  private boolean bit0;
  private boolean bit1;

  public DeviceDataUpdateRequest() {
    // empty constructor
  }

  /**
   * Create a new instance of an update request against the Apple Device Check API to update per-device data.
   *
   * @param deviceToken   the device token to identify the per-device data.
   * @param transactionId a valid transaction id for this request.
   * @param timestamp     a valid timestamp for this request.
   * @param bit0          the first of an total of 2 bits
   * @param bit1          the second of an total of 2 bits.
   */
  public DeviceDataUpdateRequest(
      String deviceToken,
      String transactionId,
      Long timestamp,
      boolean bit0,
      boolean bit1) {
    this.deviceToken = deviceToken;
    this.transactionId = transactionId;
    this.timestamp = timestamp;
    this.bit0 = bit0;
    this.bit1 = bit1;
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

  public boolean isBit0() {
    return bit0;
  }

  public void setBit0(boolean bit0) {
    this.bit0 = bit0;
  }

  public boolean isBit1() {
    return bit1;
  }

  public void setBit1(boolean bit1) {
    this.bit1 = bit1;
  }
}
