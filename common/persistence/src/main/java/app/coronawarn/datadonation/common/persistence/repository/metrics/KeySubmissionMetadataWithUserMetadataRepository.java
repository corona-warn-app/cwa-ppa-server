package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import org.springframework.data.repository.CrudRepository;

public interface KeySubmissionMetadataWithUserMetadataRepository
    extends CrudRepository<KeySubmissionMetadataWithUserMetadata, Long> {
}

