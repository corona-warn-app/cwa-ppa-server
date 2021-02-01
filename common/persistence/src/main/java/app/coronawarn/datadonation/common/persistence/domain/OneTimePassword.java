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
  @Transient
  private boolean isNew = false;

  /**
   * Constructs a {@link OneTimePassword}.
   * @param password The password.
   * @param creationTimestamp The creation timestamp.
   * @param redemptionTimestamp The redemption timestamp.
   */
  public OneTimePassword(
      @Size(min = 36, max = 36) String password, Long creationTimestamp,
      Long redemptionTimestamp) {
    this.password = password;
    this.creationTimestamp = creationTimestamp;
    this.redemptionTimestamp = redemptionTimestamp;
  }

  /**
   * Constructs a {@link OneTimePassword}. Sets the property 'isNew' to true.
   * @param password The password.
   * @param creationTimestamp The creation timestamp.
   */
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

  @Override
  public Object getId() {
    return password;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }

}
