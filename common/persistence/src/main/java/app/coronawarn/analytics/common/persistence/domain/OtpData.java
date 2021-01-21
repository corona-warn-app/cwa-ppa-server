package app.coronawarn.analytics.common.persistence.domain;

import java.time.LocalDate;
import java.util.Date;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public class OtpData {

  @Id
  @Size(min = 36, max = 36)
  private String otp;
  private LocalDate expirationDate;
  private LocalDate lastValidityCheckTime;
  private LocalDate redemptionTime;

  public OtpData(@Size(min = 36, max = 36) String otp, LocalDate expirationDate, LocalDate lastValidityCheckTime,
      LocalDate redemptionTime) {
    this.otp = otp;
    this.expirationDate = expirationDate;
    this.lastValidityCheckTime = lastValidityCheckTime;
    this.redemptionTime = redemptionTime;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
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
