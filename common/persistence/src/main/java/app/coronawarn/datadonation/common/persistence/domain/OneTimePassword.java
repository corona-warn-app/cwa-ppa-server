package app.coronawarn.datadonation.common.persistence.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public class OneTimePassword {

  @Id
  @Size(min = 36, max = 36)
  private String password;
  private LocalDateTime creationTimestamp;
  private LocalDateTime redemptionTimestamp;
  private LocalDateTime lastValidityCheckTimestamp;

  /**
   * TODO.
   * @param password a
   * @param creationTimestamp b
   * @param redemptionTimestamp c
   * @param lastValidityCheckTimestamp d
   */
  public OneTimePassword(
      @Size(min = 36, max = 36) String password, LocalDateTime creationTimestamp,
      LocalDateTime redemptionTimestamp, LocalDateTime lastValidityCheckTimestamp) {
    this.password = password;
    this.creationTimestamp = creationTimestamp;
    this.redemptionTimestamp = redemptionTimestamp;
    this.lastValidityCheckTimestamp = lastValidityCheckTimestamp;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDateTime getCreationTimestamp() {
    return creationTimestamp;
  }

  public void setCreationTimestamp(LocalDateTime creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
  }

  public LocalDateTime getRedemptionTimestamp() {
    return redemptionTimestamp;
  }

  public void setRedemptionTimestamp(LocalDateTime redemptionTimestamp) {
    this.redemptionTimestamp = redemptionTimestamp;
  }

  public LocalDateTime getLastValidityCheckTimestamp() {
    return lastValidityCheckTimestamp;
  }

  public void setLastValidityCheckTimestamp(LocalDateTime lastValidityCheckTimestamp) {
    this.lastValidityCheckTimestamp = lastValidityCheckTimestamp;
  }
}
