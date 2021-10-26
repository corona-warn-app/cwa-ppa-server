package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class SummarizedExposureWindowsWithUserMetadata extends DataDonationMetric {

  @NotNull
  private final LocalDate date;
  @NotNull
  private final String batchId;
  @NotNull
  private final Integer transmissionRiskLevel;
  @NotNull
  private final Double normalizedTime;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadata userMetadata;

  /**
   * constructs an immutable instance.
   */
  public SummarizedExposureWindowsWithUserMetadata(Long id, LocalDate date, String batchId,
      Integer transmissionRiskLevel, Double normalizedTime, UserMetadata userMetadata) {
    super(id);
    this.date = date;
    this.batchId = batchId;
    this.transmissionRiskLevel = transmissionRiskLevel;
    this.normalizedTime = normalizedTime;
    this.userMetadata = userMetadata;
  }
}
