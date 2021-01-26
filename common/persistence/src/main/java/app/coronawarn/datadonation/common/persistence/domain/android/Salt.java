package app.coronawarn.datadonation.common.persistence.domain.android;

import org.springframework.data.annotation.Id;

public class Salt {

  @Id
  private String salt;
  private Long createdAt;

  public Salt(String salt, Long createdAt) {
    this.salt = salt;
    this.createdAt = createdAt;
  }
  
  public String getSalt() {
    return salt;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "Salt [salt=" + salt + ", createdAt=" + createdAt + "]";
  }
}
