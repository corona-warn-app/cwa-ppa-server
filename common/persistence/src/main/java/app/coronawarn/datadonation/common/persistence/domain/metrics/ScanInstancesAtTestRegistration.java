package app.coronawarn.datadonation.common.persistence.domain.metrics;

import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;
import java.util.Set;

public class ScanInstancesAtTestRegistration extends DataDonationMetric {

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ScanInstance scanInstance;

  @MappedCollection(idColumn = "exposure_window_id")
  private final Set<ExposureWindowTestResult> exposureWindowTestResults;

  public ScanInstancesAtTestRegistration(Long id, ScanInstance scanInstance,
      Set<ExposureWindowTestResult> exposureWindowTestResults) {
    super(id);
    this.scanInstance = scanInstance;
    this.exposureWindowTestResults = exposureWindowTestResults;
  }
}
