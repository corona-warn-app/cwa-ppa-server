package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import org.springframework.data.repository.CrudRepository;

public interface TestResultMetadataRepository extends CrudRepository<TestResultMetadata, Long> {
}
