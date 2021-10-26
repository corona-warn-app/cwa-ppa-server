package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Set;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class ScanInstancesAtTestRegistration extends DataDonationMetric {

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ScanInstance scanInstance;

  @MappedCollection(idColumn = "exposure_window_id")
  private final Set<ExposureWindowTestResult> exposureWindowTestResults;

  /**
   * Constructs an immutable instance.
   */
  public ScanInstancesAtTestRegistration(Long id, ScanInstance scanInstance,
      Set<ExposureWindowTestResult> exposureWindowTestResults) {
    super(id);
    this.scanInstance = scanInstance;
    this.exposureWindowTestResults = exposureWindowTestResults;
  }
}
