package app.coronawarn.datadonation.common.persistence.domain;

import org.springframework.data.annotation.Id;

public class DeviceToken {

  @Id
  private Long id;

  private byte[] deviceTokenHash;

  Long createdAt;

  public DeviceToken() {
  }

  public DeviceToken(byte[] deviceTokenHash, Long createdAt) {
    this.deviceTokenHash = deviceTokenHash;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getDeviceTokenHash() {
    return deviceTokenHash;
  }

  public void setDeviceTokenHash(byte[] deviceTokenHash) {
    this.deviceTokenHash = deviceTokenHash;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }
}
