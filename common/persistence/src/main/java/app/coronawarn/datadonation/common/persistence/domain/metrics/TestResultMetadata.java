package app.coronawarn.datadonation.common.persistence.domain.metrics;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class TestResultMetadata {
  
  @Id
  private final Long id;
  /**
   * The test result reported by the client (0 to 4).
   */
  private final Integer testResult;
  /**
   * The hours since the test was registered on the device.
   */
  private final Integer hoursSinceTestRegistration;
  /**
   * The risk level on the client when the test was registered (0 to 3).
   */
  private final Integer riskLevelAtTestRegistration;
  /**
   * The number of days since the most recent encounter at the given risk level at test
   * registration.
   */
  private final Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
  /**
   * The hours since a high risk warning was issued and the test was registered.
   */
  private final Integer hoursSinceHighRiskWarningAtTestRegistration;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadata userMetadata;
  
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public TestResultMetadata(Long id, Integer testResult, Integer hoursSinceTestRegistration,
      Integer riskLevelAtTestRegistration,
      Integer daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      Integer hoursSinceHighRiskWarningAtTestRegistration, UserMetadata userMetadata,
      TechnicalMetadata technicalMetadata) {
    this.id = id;
    this.testResult = testResult;
    this.hoursSinceTestRegistration = hoursSinceTestRegistration;
    this.riskLevelAtTestRegistration = riskLevelAtTestRegistration;
    this.daysSinceMostRecentDateAtRiskLevelAtTestRegistration =
        daysSinceMostRecentDateAtRiskLevelAtTestRegistration;
    this.hoursSinceHighRiskWarningAtTestRegistration = hoursSinceHighRiskWarningAtTestRegistration;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
  }

  public Long getId() {
    return id;
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

  public UserMetadata getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }
}
