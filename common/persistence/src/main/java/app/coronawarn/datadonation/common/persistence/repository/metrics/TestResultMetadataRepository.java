package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.TestResultMetadata;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TestResultMetadataRepository extends CrudRepository<TestResultMetadata, Long> {

  @Query("select count(*) from test_result_metadata where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from test_result_metadata where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query(
      "insert into test_result_metadata (id,test_result,hours_since_test_registration,risk_level_at_test_registration,"
          + "days_since_most_recent_date_at_risk_level_at_test_registration,"
          + "hours_since_high_risk_warning_at_test_registration,federal_state,administrative_unit,age_group,"
          + "submitted_at)"
          + "values(:id,:test_result,:hours_since_test_registration,:risk_level_at_test_registration,"
          + ":days_since_most_recent_date_at_risk_level_at_test_registration,"
          + ":hours_since_high_risk_warning_at_test_registration,:federal_state,:administrative_unit,:age_group,"
          + ":submitted_at)")
  void persist(@Param("id") Long id, @Param("test_result") int testResult,
      @Param("hours_since_test_registration") int hoursSinceTestRegistration,
      @Param("risk_level_at_test_registration") int riskLevelAtTestRegistration,
      @Param("days_since_most_recent_date_at_risk_level_at_test_registration")
          int daysSinceMostRecentDateAtRiskLevelAtTestRegistration,
      @Param("hours_since_high_risk_warning_at_test_registration") int hoursSinceHighRiskWarningAtTestRegistration,
      @Param("federal_state") int federalState,
      @Param("administrative_unit") int administrativeUnit,
      @Param("age_group") int ageGroup,
      @Param("submitted_at") LocalDate submittedAt);
}
