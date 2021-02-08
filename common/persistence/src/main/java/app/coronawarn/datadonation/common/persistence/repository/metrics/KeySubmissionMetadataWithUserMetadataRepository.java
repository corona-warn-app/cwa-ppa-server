package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithUserMetadata;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KeySubmissionMetadataWithUserMetadataRepository
    extends CrudRepository<KeySubmissionMetadataWithUserMetadata, Long> {

  @Query("SELECT COUNT(*) FROM key_submission_metadata_with_user_metadata WHERE submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("DELETE FROM key_submission_metadata_with_user_metadata WHERE submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);
}

