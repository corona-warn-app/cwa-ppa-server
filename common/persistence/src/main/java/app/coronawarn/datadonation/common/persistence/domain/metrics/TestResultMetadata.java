package app.coronawarn.datadonation.common.persistence.domain.metrics;

import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;

public class TestResultMetadata extends DataDonationMetric {
  
  /**
   * The test result reported by the client (0 to 4).
   */
  @NotNull
  private final Integer testResult;
  /**
   * The hours since the test was registered on the device.
   */
  @NotNull
  private final Integer hoursSinceTestRegistration;
  /**
   * The risk level on the client when the test was registered (0 to 3).
   */
  @NotNull
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

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadata;
  
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public TestResultMetadata(Long id, Integer testResult, Integer hoursSinceTestRegistration,
      Integer riskLevelAtTestRegistration,
      Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      Integer hoursSinceHighRiskWarningAtTestRegistration, UserMetadataDetails userMetadata,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.testResult = testResult;
    this.hoursSinceTestRegistration = hoursSinceTestRegistration;
    this.riskLevelAtTestRegistration = riskLevelAtTestRegistration;
    this.daysSinceMostRecentDateAtRiskLevelAtTestRegistration =
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
    this.hoursSinceHighRiskWarningAtTestRegistration = hoursSinceHighRiskWarningAtTestRegistration;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
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

  public UserMetadataDetails getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }
}
