package app.coronawarn.datadonation.services.ios.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceData {

  boolean bit0;
  boolean bit1;
  @JsonProperty("last_update_time")
  String lastUpdated; // YYYY-MM

  public DeviceData() {
    // empty constructor
  }

  /**
   * Create new instance of per-device data.
   *
   * @param bit0        first of a total of 2 bits.
   * @param bit1        second of a total of 2 bits.
   * @param lastUpdated when this per-devie data was updated the last time.
   */
  public DeviceData(boolean bit0, boolean bit1, String lastUpdated) {
    this.bit0 = bit0;
    this.bit1 = bit1;
    this.lastUpdated = lastUpdated;
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

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
}
