package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Set;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import org.springframework.data.relational.core.mapping.MappedCollection;

public class ExposureWindowsAtTestRegistration extends DataDonationMetric {

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ExposureWindow exposureWindow;

  @MappedCollection(idColumn = "scan_instances_at_test_registration_id")
  private final Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration;

  /**
   * Constructs an immutable instance.
   */
  public  ExposureWindowsAtTestRegistration(Long id, ExposureWindow exposureWindow,
      Set<ScanInstancesAtTestRegistration> scanInstancesAtTestRegistration) {
    super(id);
    this.exposureWindow = exposureWindow;
    this.scanInstancesAtTestRegistration = scanInstancesAtTestRegistration;
  }
}
