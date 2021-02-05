package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import org.springframework.data.repository.CrudRepository;

public interface ExposureRiskMetadataRepository
    extends CrudRepository<ExposureRiskMetadata, Long> {
}
