package app.coronawarn.datadonation.common.persistence.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("android_id")
public class AndroidId {

  /**
   * Peppered Android ID.
   */
  @Id
  private String id;
  Long createdAt;
  Long expirationDate;
  Long lastUsedSrs;

  public AndroidId() {
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public Long getExpirationDate() {
    return expirationDate;
  }

  public String getId() {
    return id;
  }

  public Long getLastUsedSrs() {
    return lastUsedSrs;
  }

  public void setCreatedAt(final Long createdAt) {
    this.createdAt = createdAt;
  }

  public void setExpirationDate(final Long expirationDate) {
    this.expirationDate = expirationDate;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public void setLastUsedSrs(final Long lastUsedSrs) {
    this.lastUsedSrs = lastUsedSrs;
  }
}
