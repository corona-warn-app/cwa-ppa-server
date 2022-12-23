package app.coronawarn.datadonation.common.persistence.domain;

import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("android_id")
public class AndroidId {

  /**
   * Peppered Android ID.
   */
  @Id
  @Size(min = 44, max = 44)
  private String id;

  Long expirationDate;

  Long lastUsedSrs;

  public Long getExpirationDate() {
    return expirationDate;
  }

  public String getId() {
    return id;
  }

  public Long getLastUsedSrs() {
    return lastUsedSrs;
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

  @Override
  public String toString() {
    return getId();
  }
}
