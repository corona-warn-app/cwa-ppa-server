package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * The following properties are technical metadata that are inlined per metrics record to avoid
 * correlating entries from the same submission.
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
   * True if attribute evaluationType from PPAC for Android contains BASIC, false otherwise; null if
   * for iOS.
   */
  private final Boolean androidPpacEvaluationTypeBasic;
  /**
   * True if attribute evaluationType from PPAC for Android contains HARDWARE_BACKED, false
   * otherwise.
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TechnicalMetadata that = (TechnicalMetadata) o;
    return Objects.equals(submittedAt, that.submittedAt)
        && Objects.equals(androidPpacBasicIntegrity, that.androidPpacBasicIntegrity)
        && Objects.equals(androidPpacCtsProfileMatch, that.androidPpacCtsProfileMatch)
        && Objects.equals(androidPpacEvaluationTypeBasic, that.androidPpacEvaluationTypeBasic)
        && Objects.equals(androidPpacEvaluationTypeHardwareBacked,
          that.androidPpacEvaluationTypeHardwareBacked);
  }

  @Override
  public int hashCode() {
    return Objects.hash(submittedAt, androidPpacBasicIntegrity, androidPpacCtsProfileMatch,
        androidPpacEvaluationTypeBasic, androidPpacEvaluationTypeHardwareBacked);
  }
}
