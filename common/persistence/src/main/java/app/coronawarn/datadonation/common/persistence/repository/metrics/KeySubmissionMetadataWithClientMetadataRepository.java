package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import org.springframework.data.repository.CrudRepository;

public interface KeySubmissionMetadataWithClientMetadataRepository
    extends CrudRepository<KeySubmissionMetadataWithClientMetadata, Long> {
}
