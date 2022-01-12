package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import java.util.Objects;

public class TestResultMetadata extends DataDonationMetric {

  private static final long MIN_RISK_LEVEL = 0;
  private static final long MAX_RISK_LEVEL = 3;

  /**
   * The test result reported by the client (0 to 8).
   */
  @NotNull
  private final Integer testResult;

  /**
   * The hours since the test was registered on the device.
   */
  @NotNull
  private final Integer hoursSinceTestRegistration;

  /**
   * The risk level on the client when the test was registered ({@value #MIN_RISK_LEVEL} to {@value #MAX_RISK_LEVEL}).
   */
  @NotNull
  @Range(min = MIN_RISK_LEVEL, max = MAX_RISK_LEVEL,
      message = "Risk Level must be in between " + MIN_RISK_LEVEL + " and " + MAX_RISK_LEVEL + ".")
  private final Integer riskLevelAtTestRegistration;

  /**
   * The number of days since the most recent encounter at the given risk level at test
   * registration.
   */
  @NotNull
  private final Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration;

  /**
   * The hours since a high risk warning was issued and the test was registered.
   */
  @NotNull
  private final Integer hoursSinceHighRiskWarningAtTestRegistration;

  /**
   * The risk level on the client when check-in-based presence tracing test was registered
   * ({@value #MIN_RISK_LEVEL} to {@value #MAX_RISK_LEVEL}).
   */
  @Range(min = MIN_RISK_LEVEL, max = MAX_RISK_LEVEL,
      message = "Risk Level must be in between " + MIN_RISK_LEVEL + " and " + MAX_RISK_LEVEL + ".")
  private final Integer ptRiskLevel;

  /**
   * The number of days since the most recent encounter at the given risk level at check-in-based presence
   * tracing test registration.
   */
  private final Integer ptDaysSinceMostRecentDateAtRiskLevel;

  /**
   * The hours since a high risk warning was issued and check-in-based presence tracing test was registered.
   */
  private final Integer ptHoursSinceHighRiskWarning;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final CwaVersionMetadata cwaVersionMetadata;

  /**
   * Constructs an immutable instance.
   */
  public TestResultMetadata(Long id, Integer testResult, Integer hoursSinceTestRegistration, //NOSONAR parameter no
      Integer riskLevelAtTestRegistration,
      Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      Integer hoursSinceHighRiskWarningAtTestRegistration,
      Integer ptRiskLevel,
      Integer ptDaysSinceMostRecentDateAtRiskLevel,
      Integer ptHoursSinceHighRiskWarning,
      UserMetadataDetails userMetadata,
      TechnicalMetadata technicalMetadata,
      CwaVersionMetadata cwaVersionMetadata) {
    super(id);
    this.testResult = testResult;
    this.hoursSinceTestRegistration = hoursSinceTestRegistration;
    this.riskLevelAtTestRegistration = riskLevelAtTestRegistration;
    this.daysSinceMostRecentDateAtRiskLevelAtTestRegistration =
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
    this.hoursSinceHighRiskWarningAtTestRegistration = hoursSinceHighRiskWarningAtTestRegistration;
    this.ptRiskLevel = ptRiskLevel;
    this.ptDaysSinceMostRecentDateAtRiskLevel =
        ptDaysSinceMostRecentDateAtRiskLevel;
    this.ptHoursSinceHighRiskWarning = ptHoursSinceHighRiskWarning;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
    this.cwaVersionMetadata = cwaVersionMetadata;
  }

  public CwaVersionMetadata getCwaVersionMetadata() {
    return cwaVersionMetadata;
  }

  public Integer getTestResult() {
    return testResult;
  }

  public Integer getHoursSinceTestRegistration() {
    return hoursSinceTestRegistration;
  }

  public Integer getRiskLevelAtTestRegistration() {
    return riskLevelAtTestRegistration;
  }

  public Integer getDaysSinceMostRecentDateAtRiskLevelAtTestRegistration() {
    return daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
  }

  public Integer getHoursSinceHighRiskWarningAtTestRegistration() {
    return hoursSinceHighRiskWarningAtTestRegistration;
  }

  public Integer getPtRiskLevel() {
    return ptRiskLevel;
  }

  public Integer getPtDaysSinceMostRecentDateAtRiskLevel() {
    return ptDaysSinceMostRecentDateAtRiskLevel;
  }

  public Integer getPtHoursSinceHighRiskWarning() {
    return ptHoursSinceHighRiskWarning;
  }

  public UserMetadataDetails getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    TestResultMetadata that = (TestResultMetadata) o;
    return Objects.equals(id, that.id)
        && Objects.equals(testResult, that.testResult)
        && Objects.equals(hoursSinceTestRegistration, that.hoursSinceTestRegistration)
        && Objects.equals(riskLevelAtTestRegistration, that.riskLevelAtTestRegistration)
        && Objects.equals(daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
          that.daysSinceMostRecentDateAtRiskLevelAtTestRegistration)
        && Objects.equals(hoursSinceHighRiskWarningAtTestRegistration,
          that.hoursSinceHighRiskWarningAtTestRegistration)
        && Objects.equals(ptRiskLevel, that.ptRiskLevel)
        && Objects.equals(ptDaysSinceMostRecentDateAtRiskLevel,
          that.ptDaysSinceMostRecentDateAtRiskLevel)
        && Objects.equals(ptHoursSinceHighRiskWarning, that.ptHoursSinceHighRiskWarning)
        && Objects.equals(userMetadata, that.userMetadata)
        && Objects.equals(technicalMetadata, that.technicalMetadata)
        && Objects.equals(cwaVersionMetadata, that.cwaVersionMetadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, testResult, hoursSinceTestRegistration, riskLevelAtTestRegistration,
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration, hoursSinceHighRiskWarningAtTestRegistration, ptRiskLevel,
        ptDaysSinceMostRecentDateAtRiskLevel, ptHoursSinceHighRiskWarning, userMetadata, technicalMetadata,
        cwaVersionMetadata);
  }
}
