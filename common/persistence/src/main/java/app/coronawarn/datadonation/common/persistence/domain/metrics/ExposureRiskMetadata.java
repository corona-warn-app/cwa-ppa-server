package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import app.coronawarn.datadonation.common.validation.DateInRange;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ExposureRiskMetadata extends DataDonationMetric {

  private static final long MIN_RISK_LEVEL = 0;
  private static final long MAX_RISK_LEVEL = 3;

  /**
   * The risk level reported by the client ({@value #MIN_RISK_LEVEL} to {@value #MAX_RISK_LEVEL}).
   */
  @Range(min = MIN_RISK_LEVEL, max = MAX_RISK_LEVEL,
      message = "Risk Level must be in between " + MIN_RISK_LEVEL + " and " + MAX_RISK_LEVEL + ".")
  private final Integer riskLevel;

  /**
   * The risk level reported by the client from check-in-based presence tracing. ({@value #MIN_RISK_LEVEL} to {@value
   * #MAX_RISK_LEVEL}).
   */
  @Range(min = MIN_RISK_LEVEL, max = MAX_RISK_LEVEL,
      message = "Risk Level must be in between " + MIN_RISK_LEVEL + " and " + MAX_RISK_LEVEL + ".")
  private final Integer ptRiskLevel;

  /**
   * Boolean to indicate if the Risk Level changed compared to the previous submission of the client.
   */
  @NotNull
  private final Boolean riskLevelChanged;
  /**
   * Boolean to indicate if the Risk Level changed compared to the previous submission of the client.
   */
  private final Boolean ptRiskLevelChanged;
  /**
   * The date of the most recent encounter at the given risk level (i.e. what is displayed on the risk card)
   */
  @NotNull
  @DateInRange(from = "1969-12-31", till = "2100-01-01")
  private final LocalDate mostRecentDateAtRiskLevel;

  /**
   * The date of the most recent encounter at the given risk level (i.e. what is displayed on the risk card)
   */
  @DateInRange(from = "1969-12-31", till = "2100-01-01")
  private final LocalDate ptMostRecentDateAtRiskLevel;
  /**
   * Boolean to indicate if the date changed compared to the previous submission of the client.
   */
  @NotNull
  private final Boolean mostRecentDateChanged;
  /**
   * Boolean to indicate if the date changed compared to the previous submission of the client.
   */
  private final Boolean ptMostRecentDateChanged;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final CwaVersionMetadata cwaVersionMetadata;

  /**
   * constructs an immutable instance.
   */
  public ExposureRiskMetadata(Long id, Integer riskLevel, //NOSONAR number of parameters
      Boolean riskLevelChanged,
      LocalDate mostRecentDateAtRiskLevel,
      Boolean mostRecentDateChanged,
      Integer ptRiskLevel,
      Boolean ptRiskLevelChanged,
      LocalDate ptMostRecentDateAtRiskLevel,
      Boolean ptMostRecentDateChanged,
      UserMetadataDetails userMetadata,
      TechnicalMetadata technicalMetadata,
      CwaVersionMetadata cwaVersionMetadata) {
    super(id);
    this.riskLevel = riskLevel;
    this.ptRiskLevel = ptRiskLevel;
    this.riskLevelChanged = riskLevelChanged;
    this.ptRiskLevelChanged = ptRiskLevelChanged;
    this.mostRecentDateAtRiskLevel = mostRecentDateAtRiskLevel;
    this.ptMostRecentDateAtRiskLevel = ptMostRecentDateAtRiskLevel;
    this.mostRecentDateChanged = mostRecentDateChanged;
    this.ptMostRecentDateChanged = ptMostRecentDateChanged;
    this.userMetadata = userMetadata;
    this.technicalMetadata = technicalMetadata;
    this.cwaVersionMetadata = cwaVersionMetadata;
  }

  public Integer getRiskLevel() {
    return riskLevel;
  }

  public Boolean getRiskLevelChanged() {
    return riskLevelChanged;
  }

  public LocalDate getMostRecentDateAtRiskLevel() {
    return mostRecentDateAtRiskLevel;
  }

  public Boolean getMostRecentDateChanged() {
    return mostRecentDateChanged;
  }

  public UserMetadataDetails getUserMetadata() {
    return userMetadata;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  public Integer getPtRiskLevel() {
    return ptRiskLevel;
  }

  public Boolean getPtRiskLevelChanged() {
    return ptRiskLevelChanged;
  }

  public LocalDate getPtMostRecentDateAtRiskLevel() {
    return ptMostRecentDateAtRiskLevel;
  }

  public Boolean getPtMostRecentDateChanged() {
    return ptMostRecentDateChanged;
  }

  public CwaVersionMetadata getCwaVersionMetadata() {
    return cwaVersionMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, mostRecentDateAtRiskLevel, mostRecentDateChanged, riskLevel,
        riskLevelChanged, ptRiskLevel, ptRiskLevelChanged, ptMostRecentDateAtRiskLevel, ptRiskLevelChanged,
        technicalMetadata, userMetadata, cwaVersionMetadata);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    ExposureRiskMetadata that = (ExposureRiskMetadata) obj;
    return Objects.equals(riskLevelChanged, that.riskLevelChanged)
        && Objects.equals(ptRiskLevelChanged, that.ptRiskLevelChanged)
        && Objects.equals(mostRecentDateChanged, that.mostRecentDateChanged)
        && Objects.equals(ptMostRecentDateChanged, that.ptMostRecentDateChanged)
        && Objects.equals(riskLevel, that.riskLevel)
        && Objects.equals(ptRiskLevel, that.ptRiskLevel)
        && Objects.equals(mostRecentDateAtRiskLevel, that.mostRecentDateAtRiskLevel)
        && Objects.equals(ptMostRecentDateAtRiskLevel, that.ptMostRecentDateAtRiskLevel)
        && Objects.equals(userMetadata, that.userMetadata)
        && Objects.equals(technicalMetadata, that.technicalMetadata)
        && Objects.equals(cwaVersionMetadata, that.cwaVersionMetadata);
  }
}
