package app.coronawarn.datadonation.common.persistence.domain.metrics;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

/**
 * The following properties are technical metadata that are inlined per metrics record to avoid correlating entries from
 * the same submission.
 */
public class TechnicalMetadata {

  /**
   * The date (no time information) of when the record was submitted to the server.
   */
  @NotNull
  private final LocalDate submittedAt;
  /**
   * Value of attribute basicIntegrity from PPAC for Android; null if for iOS.
   */
  private final Boolean androidPpacBasicIntegrity;
  /**
   * Value of attribute ctsProfileMatch from PPAC for Android; null if for iOS.
   */
  private final Boolean androidPpacCtsProfileMatch;
  /**
   * True if attribute evaluationType from PPAC for Android contains BASIC, false otherwise; null if for iOS.
   */
  private final Boolean androidPpacEvaluationTypeBasic;
  /**
   * True if attribute evaluationType from PPAC for Android contains HARDWARE_BACKED, false otherwise.
   */
  private final Boolean androidPpacEvaluationTypeHardwareBacked;

  /**
   * Construct an immutable instance.
   */
  public TechnicalMetadata(LocalDate submittedAt, Boolean androidPpacBasicIntegrity,
      Boolean androidPpacCtsProfileMatch, Boolean androidPpacEvaluationTypeBasic,
      Boolean androidPpacEvaluationTypeHardwareBacked) {
    this.submittedAt = submittedAt;
    this.androidPpacBasicIntegrity = androidPpacBasicIntegrity;
    this.androidPpacCtsProfileMatch = androidPpacCtsProfileMatch;
    this.androidPpacEvaluationTypeBasic = androidPpacEvaluationTypeBasic;
    this.androidPpacEvaluationTypeHardwareBacked = androidPpacEvaluationTypeHardwareBacked;
  }

  public LocalDate getSubmittedAt() {
    return submittedAt;
  }

  public Boolean getAndroidPpacBasicIntegrity() {
    return androidPpacBasicIntegrity;
  }

  public Boolean getAndroidPpacCtsProfileMatch() {
    return androidPpacCtsProfileMatch;
  }

  public Boolean getAndroidPpacEvaluationTypeBasic() {
    return androidPpacEvaluationTypeBasic;
  }

  public Boolean getAndroidPpacEvaluationTypeHardwareBacked() {
    return androidPpacEvaluationTypeHardwareBacked;
  }

  public static TechnicalMetadata newEmptyInstance() {
    return new TechnicalMetadata(LocalDate.now(ZoneId.of("UTC")), null, null, null, null);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    TechnicalMetadata that = (TechnicalMetadata) obj;
    return Objects.equals(androidPpacBasicIntegrity, that.androidPpacBasicIntegrity)
        && Objects.equals(androidPpacCtsProfileMatch, that.androidPpacCtsProfileMatch)
        && Objects.equals(androidPpacEvaluationTypeBasic, that.androidPpacEvaluationTypeBasic)
        && Objects.equals(androidPpacEvaluationTypeHardwareBacked,
            that.androidPpacEvaluationTypeHardwareBacked)
        && Objects.equals(submittedAt, that.submittedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(submittedAt, androidPpacBasicIntegrity, androidPpacCtsProfileMatch,
        androidPpacEvaluationTypeBasic, androidPpacEvaluationTypeHardwareBacked);
  }
}
