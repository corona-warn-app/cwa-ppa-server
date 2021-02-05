package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;

public interface KeySubmissionMetadataWithUserMetadataRepository
    extends CrudRepository<KeySubmissionMetadataWithUserMetadata, Long> {

  @Query("select count(*) from key_submission_metadata_with_client_metadata where submitted_at < :threshold")
  int countOlderThan(LocalDate threshold);

  @Modifying
  @Query("delete from key_submission_metadata_with_client_metadata where submitted_at < :threshold")
  void deleteOlderThan(LocalDate threshold);
}

