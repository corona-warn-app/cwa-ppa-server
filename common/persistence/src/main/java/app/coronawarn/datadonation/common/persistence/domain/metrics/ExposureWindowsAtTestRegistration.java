package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Set;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class ExposureWindowsAtTestRegistration extends DataDonationMetric {

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ExposureWindow exposureWindow;

  @MappedCollection(idColumn = "exposure_window_test_result_id")
  private final Set<ExposureWindowTestResult> exposureWindowTestResults;

  /**
   * Constructs an immutable instance.
   */
  public ExposureWindowsAtTestRegistration(Long id, ExposureWindow exposureWindow,
      Set<ExposureWindowTestResult> exposureWindowTestResults) {
    super(id);
    this.exposureWindow = exposureWindow;
    this.exposureWindowTestResults = exposureWindowTestResults;
  }
}
