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

  @Modifying
  @Query("INSERT INTO key_submission_metadata_with_user_metadata (id, submitted, submitted_after_symptom_flow,  "
      + "submitted_with_teletan,  hours_since_reception_of_test_result,  "
      + "hours_since_test_registration, days_since_most_recent_date_at_risk_level_at_test_registration, "
      + "hours_since_high_risk_warning_at_test_registration, "
      + "federal_state, age_group, administrative_unit, submitted_at)"
      + "VALUES (:id, :submitted, :submitted_after_symptom_flow, :submitted_with_teletan, "
      + ":hours_since_reception_of_test_result, :hours_since_test_registration, "
      + ":days_since_most_recent_date_at_risk_level_at_test_registration, "
      + ":hours_since_high_risk_warning_at_test_registration, :federal_state, :age_group, :administrative_unit, "
      + ":submitted_at)")
  void persist(
      @Param("id") Long id,
      @Param("submitted") boolean submitted,
      @Param("submitted_after_symptom_flow") boolean submittedAfterSymptomFlow,
      @Param("submitted_with_teletan") boolean submittedWithTeletan,
      @Param("hours_since_reception_of_test_result") int hoursSinceReceptionOfTestResult,
      @Param("hours_since_test_registration") int hoursSinceTestRegistration,
      @Param("days_since_most_recent_date_at_risk_level_at_test_registration")
          int daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      @Param("hours_since_high_risk_warning_at_test_registration") int hoursSinceHighRisWarningAtTestRegistration,
      @Param("federal_state") int federalState,
      @Param("age_group") int ageGroup,
      @Param("administrative_unit") int administrativeUnit,
      @Param("submitted_at") LocalDate submittedAt);
}

