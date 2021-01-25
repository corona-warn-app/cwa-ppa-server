package app.coronawarn.datadonation.common.persistence.domain;

import org.springframework.data.annotation.Id;

public class DeviceToken {

  @Id
  private String deviceTokenHash;

  Long createdAt;

  public String getDeviceTokenHash() {
    return deviceTokenHash;
  }

  public void setDeviceTokenHash(String deviceTokenHash) {
    this.deviceTokenHash = deviceTokenHash;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }
}
