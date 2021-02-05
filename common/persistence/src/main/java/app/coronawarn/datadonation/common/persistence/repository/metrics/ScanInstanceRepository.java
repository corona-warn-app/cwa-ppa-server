package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import org.springframework.data.repository.CrudRepository;

public interface ScanInstanceRepository extends CrudRepository<ScanInstance, Long> {
}
