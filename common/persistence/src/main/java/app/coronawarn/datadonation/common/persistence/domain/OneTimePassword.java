package app.coronawarn.datadonation.common.persistence.domain;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public class OneTimePassword {

  @Id
  @Size(min = 36, max = 36)
  private String password;
  private Long redemptionTimestamp;
  private Long expirationTimestamp;
  private Boolean androidPpacBasicIntegrity;
  private Boolean androidPpacCtsProfileMatch;
  private Boolean androidPpacEvaluationTypeBasic;
  private Boolean androidPpacEvaluationTypeHardwareBacked;
  private Boolean androidPpacAdvice;

  /**
   * TODO.
   *
   * @param password            The otp to store.
   * @param redemptionTimestamp The point in time, when the otp was redeemed.
   * @param expirationTimestamp The point in time, when the otp expires.
   */
  public OneTimePassword(
      @Size(min = 36, max = 36) String password, Long redemptionTimestamp,
      Long expirationTimestamp) {
    this.password = password;
    this.redemptionTimestamp = redemptionTimestamp;
    this.expirationTimestamp = expirationTimestamp;
  }

  /**
   * TODO.
   *
   * @param password       The otp to store.
   * @param redemptionTime The point in time, when the otp was redeemed.
   * @param expirationTime The point in time, when the otp expires.
   */
  public OneTimePassword(
      @Size(min = 36, max = 36) String password, LocalDateTime redemptionTime,
      LocalDateTime expirationTime) {
    this.password = password;
    this.redemptionTimestamp =
        redemptionTime != null ? redemptionTime.toInstant(ZoneOffset.UTC).getEpochSecond() : null;
    this.expirationTimestamp =
        expirationTime != null ? expirationTime.toInstant(ZoneOffset.UTC).getEpochSecond() : null;
  }

  /**
   * TODO.
   *
   * @param password                                The otp to store.
   * @param redemptionTimestamp                     The point in time, when the otp was redeemed.
   * @param expirationTimestamp                     The point in time, when the otp expires.
   * @param androidPpacBasicIntegrity               The Android PPAC Basic Integrity.
   * @param androidPpacCtsProfileMatch              The Android PPAC CTS Profile Match.
   * @param androidPpacEvaluationTypeBasic          Android PPAC Evaluation Type Basic.
   * @param androidPpacEvaluationTypeHardwareBacked Android PPAC Evaluation Type Hardware-Backed.
   * @param androidPpacAdvice                       Android PPAC Advice.
   */
  public OneTimePassword(
      @Size(min = 36, max = 36) String password, Long redemptionTimestamp,
      Long expirationTimestamp, Boolean androidPpacBasicIntegrity,
      Boolean androidPpacCtsProfileMatch, Boolean androidPpacEvaluationTypeBasic,
      Boolean androidPpacEvaluationTypeHardwareBacked, Boolean androidPpacAdvice) {
    this(password, redemptionTimestamp, expirationTimestamp);
    this.androidPpacBasicIntegrity = androidPpacBasicIntegrity;
    this.androidPpacCtsProfileMatch = androidPpacCtsProfileMatch;
    this.androidPpacEvaluationTypeBasic = androidPpacEvaluationTypeBasic;
    this.androidPpacEvaluationTypeHardwareBacked = androidPpacEvaluationTypeHardwareBacked;
    this.androidPpacAdvice = androidPpacAdvice;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long getRedemptionTimestamp() {
    return redemptionTimestamp;
  }

  public void setRedemptionTimestamp(Long redemptionTimestamp) {
    this.redemptionTimestamp = redemptionTimestamp;
  }

  public Long getExpirationTimestamp() {
    return expirationTimestamp;
  }

  public void setExpirationTimestamp(Long expirationTimestamp) {
    this.expirationTimestamp = expirationTimestamp;
  }

  public Boolean getAndroidPpacBasicIntegrity() {
    return androidPpacBasicIntegrity;
  }

  public void setAndroidPpacBasicIntegrity(Boolean androidPpacBasicIntegrity) {
    this.androidPpacBasicIntegrity = androidPpacBasicIntegrity;
  }

  public Boolean getAndroidPpacCtsProfileMatch() {
    return androidPpacCtsProfileMatch;
  }

  public void setAndroidPpacCtsProfileMatch(Boolean androidPpacCtsProfileMatch) {
    this.androidPpacCtsProfileMatch = androidPpacCtsProfileMatch;
  }

  public Boolean getAndroidPpacEvaluationTypeBasic() {
    return androidPpacEvaluationTypeBasic;
  }

  public void setAndroidPpacEvaluationTypeBasic(Boolean androidPpacEvaluationTypeBasic) {
    this.androidPpacEvaluationTypeBasic = androidPpacEvaluationTypeBasic;
  }

  public Boolean getAndroidPpacEvaluationTypeHardwareBacked() {
    return androidPpacEvaluationTypeHardwareBacked;
  }

  public void setAndroidPpacEvaluationTypeHardwareBacked(
      Boolean androidPpacEvaluationTypeHardwareBacked) {
    this.androidPpacEvaluationTypeHardwareBacked = androidPpacEvaluationTypeHardwareBacked;
  }

  public Boolean getAndroidPpacAdvice() {
    return androidPpacAdvice;
  }

  public void setAndroidPpacAdvice(Boolean androidPpacAdvice) {
    this.androidPpacAdvice = androidPpacAdvice;
  }
}
