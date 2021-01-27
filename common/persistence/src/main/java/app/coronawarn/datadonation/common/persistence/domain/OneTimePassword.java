package app.coronawarn.datadonation.common.persistence.domain;

import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

public class OneTimePassword implements Persistable {

  @Id
  @Size(min = 36, max = 36)
  private String password;
  private Long creationTimestamp;
  private Long redemptionTimestamp;
  private Long lastValidityCheckTimestamp;
  @Transient
  private boolean isNew = false;

  /**
   * TODO.
   *
   * @param password                   a
   * @param creationTimestamp          b
   * @param redemptionTimestamp        c
   * @param lastValidityCheckTimestamp d
   */
  public OneTimePassword(
      @Size(min = 36, max = 36) String password, Long creationTimestamp,
      Long redemptionTimestamp, Long lastValidityCheckTimestamp) {
    this.password = password;
    this.creationTimestamp = creationTimestamp;
    this.redemptionTimestamp = redemptionTimestamp;
    this.lastValidityCheckTimestamp = lastValidityCheckTimestamp;
  }

  public OneTimePassword(
      @Size(min = 36, max = 36) String password, Long creationTimestamp) {
    this.password = password;
    this.creationTimestamp = creationTimestamp;
    this.isNew = true;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(Long creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public Long getRedemptionTimestamp() {
    return redemptionTimestamp;
  }

  public void setRedemptionTimestamp(Long redemptionTimestamp) {
    this.redemptionTimestamp = redemptionTimestamp;
  }

  public Long getLastValidityCheckTimestamp() {
    return lastValidityCheckTimestamp;
  }

  public void setLastValidityCheckTimestamp(Long lastValidityCheckTimestamp) {
    this.lastValidityCheckTimestamp = lastValidityCheckTimestamp;
  }

  @Override
  public Object getId() {
    return password;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }

}
