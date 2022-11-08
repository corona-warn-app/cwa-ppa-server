package app.coronawarn.datadonation.common.persistence.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("android_id")
public class AndroidId {

  @Id
  private String id;
  Long createdAt;
  Long expirationDate;
  Long lastUsedForSrs;

  public AndroidId() {
  }

  public AndroidId(Long createdAt) {
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }

  public Long getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Long expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Long getLastUsedForSrs() {
    return lastUsedForSrs;
  }

  public void setLastUsedForSrs(Long lastUsedForSrs) {
    this.lastUsedForSrs = lastUsedForSrs;
  }
}
