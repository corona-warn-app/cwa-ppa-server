package app.coronawarn.analytics.common.persistence.domain;

import java.time.LocalDate;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public class OneTimePassword {

  @Id
  @Size(min = 36, max = 36)
  private String password;
  private LocalDate expirationDate;
  private LocalDate lastValidityCheckTime;
  private LocalDate redemptionTime;

  /**
   * Constructs a OtpData object.
   *
   * @param password              string ID value
   * @param expirationDate        expiry date for OTP
   * @param lastValidityCheckTime last verified date time for OTP
   * @param redemptionTime        .
   */
  public OneTimePassword(@Size(min = 36, max = 36) String password, LocalDate expirationDate,
      LocalDate lastValidityCheckTime,
      LocalDate redemptionTime) {
    this.password = password;
    this.expirationDate = expirationDate;
    this.lastValidityCheckTime = lastValidityCheckTime;
    this.redemptionTime = redemptionTime;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public LocalDate getLastValidityCheckTime() {
    return lastValidityCheckTime;
  }

  public void setLastValidityCheckTime(LocalDate lastValidityCheckTime) {
    this.lastValidityCheckTime = lastValidityCheckTime;
  }

  public LocalDate getRedemptionTime() {
    return redemptionTime;
  }

  public void setRedemptionTime(LocalDate redemptionTime) {
    this.redemptionTime = redemptionTime;
  }
}
