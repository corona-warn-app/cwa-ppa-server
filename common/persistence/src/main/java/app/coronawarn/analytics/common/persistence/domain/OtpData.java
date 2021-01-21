package app.coronawarn.analytics.common.persistence.domain;

import java.util.Date;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public class OtpData {

  @Id
  @Size(min = 36, max = 36)
  private String otp;
  private Date expirationDate;
  private Date lastValidityCheckTime;
  private Date redemptionTime;

  public OtpData(@Size(min = 36, max = 36) String otp, Date expirationDate, Date lastValidityCheckTime,
      Date redemptionTime) {
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

  public Date getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Date getLastValidityCheckTime() {
    return lastValidityCheckTime;
  }

  public void setLastValidityCheckTime(Date lastValidityCheckTime) {
    this.lastValidityCheckTime = lastValidityCheckTime;
  }

  public Date getRedemptionTime() {
    return redemptionTime;
  }

  public void setRedemptionTime(Date redemptionTime) {
    this.redemptionTime = redemptionTime;
  }
}
