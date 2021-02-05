package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KeySubmissionMetadataWithClientMetadataRepository
    extends CrudRepository<KeySubmissionMetadataWithClientMetadata, Long> {

  @Query("select count(*) from key_submission_metadata_with_client_metadata where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from key_submission_metadata_with_client_metadata where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("INSERT INTO key_submission_metadata_with_client_metadata (id, submitted, submitted_in_background, "
      + "submitted_after_cancel, submitted_after_symptom_flow, advanced_consent_given, last_submission_flow_screen, "
      + "cwa_version_major, cwa_version_minor, cwa_version_patch, app_config_etag, submitted_at)"
      + "VALUES (:id, :submitted, :submitted_in_background, :submitted_after_cancel, :submitted_after_symptom_flow, "
      + ":advanced_consent_given, :last_submission_flow_screen, :cwa_version_major, :cwa_version_minor, "
      + ":cwa_version_patch, :app_config_etag, :submitted_at)")
  void persist(
      @Param("id") Long id,
      @Param("submitted") boolean submitted,
      @Param("submitted_in_background") boolean submittedInBackground,
      @Param("submitted_after_cancel") boolean submittedAfterCancel,
      @Param("submitted_after_symptom_flow") boolean submittedAfterSymptomFlow,
      @Param("advanced_consent_given") boolean advancedConsentGiven,
      @Param("last_submission_flow_screen") int lastSubmissionFlowScreen,
      @Param("cwa_version_major") int cwaVersionMajor,
      @Param("cwa_version_minor") int cwaVersionMinor,
      @Param("cwa_version_patch") int cwaVersionPatch,
      @Param("app_config_etag") String appConfigEtag,
      @Param("submitted_at") LocalDate submittedAt);
}
