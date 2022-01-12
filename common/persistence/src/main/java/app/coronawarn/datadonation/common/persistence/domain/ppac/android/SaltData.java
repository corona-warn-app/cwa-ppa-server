package app.coronawarn.datadonation.common.persistence.domain.ppac.android;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("salt")
public class SaltData {

  @Id
  private String salt;
  private final Long createdAt;

  public SaltData(String salt, Long createdAt) {
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
