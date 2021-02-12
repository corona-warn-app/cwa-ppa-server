package app.coronawarn.datadonation.common.persistence.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

public class OneTimePassword implements Persistable<String> {

  @Id
  @Size(min = 36, max = 36)
  private String password;
  private Long redemptionTimestamp;
  private Long expirationTimestamp;
  private Boolean androidPpacBasicIntegrity;
  private Boolean androidPpacCtsProfileMatch;
  private Boolean androidPpacEvaluationTypeBasic;
  private Boolean androidPpacEvaluationTypeHardwareBacked;
  @Transient
  private boolean isNew = false;

  /**
   * Constructs the {@link OneTimePassword}.
   *
   * @param password The otp to store.
   */
  public OneTimePassword(@Size(min = 36, max = 36) String password) {
    this.password = password;
    this.isNew = true;
  }

  /**
   * Constructs the OneTimePassword.
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
  public OneTimePassword(@Size(min = 36, max = 36) String password, Long redemptionTimestamp, Long expirationTimestamp,
      Boolean androidPpacBasicIntegrity, Boolean androidPpacCtsProfileMatch, Boolean androidPpacEvaluationTypeBasic,
      Boolean androidPpacEvaluationTypeHardwareBacked) {
    this.password = password;
    this.redemptionTimestamp = redemptionTimestamp;
    this.expirationTimestamp = expirationTimestamp;
    this.androidPpacBasicIntegrity = androidPpacBasicIntegrity;
    this.androidPpacCtsProfileMatch = androidPpacCtsProfileMatch;
    this.androidPpacEvaluationTypeBasic = androidPpacEvaluationTypeBasic;
    this.androidPpacEvaluationTypeHardwareBacked = androidPpacEvaluationTypeHardwareBacked;
    this.isNew = false;
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

  public void setRedemptionTimestamp(LocalDateTime redemptionTime) {
    this.redemptionTimestamp = epochSecond(redemptionTime);
  }

  public Long getExpirationTimestamp() {
    return expirationTimestamp;
  }

  public void setExpirationTimestamp(Long expirationTimestamp) {
    this.expirationTimestamp = expirationTimestamp;
  }

  public void setExpirationTimestamp(LocalDateTime expirationTime) {
    this.expirationTimestamp = epochSecond(expirationTime);
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

  public void setAndroidPpacEvaluationTypeHardwareBacked(Boolean androidPpacEvaluationTypeHardwareBacked) {
    this.androidPpacEvaluationTypeHardwareBacked = androidPpacEvaluationTypeHardwareBacked;
  }

  @Override
  public String getId() {
    return password;
  }

  @Override
  public boolean isNew() {
    return isNew;
  }

  @Nullable
  private Long epochSecond(final ChronoLocalDateTime<LocalDate> time) {
    if (time == null) {
      return null;
    }
    return time.toInstant(ZoneOffset.UTC).getEpochSecond();
  }
}
