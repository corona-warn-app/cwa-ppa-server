package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExposureRiskMetadataRepository
    extends CrudRepository<ExposureRiskMetadata, Long> {

  @Query("select count(*) from exposure_risk_metadata where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from exposure_risk_metadata where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("INSERT INTO exposure_risk_metadata (id, risk_level, risk_level_changed, "
      + "most_recent_date_at_risk_level, most_recent_date_changed, federal_state, "
      + "administrative_unit, age_group, submitted_at)"
      + "VALUES (:id, :risk_level, :risk_level_changed, :most_recent_date_at_risk_level, :most_recent_date_changed, "
      + ":federal_state, :administrative_unit, :age_group, "
      + ":submitted_at)")
  void persist(
      @Param("id") Long id,
      @Param("risk_level") int riskLevel,
      @Param("risk_level_changed") boolean riskLevelChanged,
      @Param("most_recent_date_at_risk_level") LocalDate mostRecentDateAtRiskLevel,
      @Param("most_recent_date_changed") boolean mostRecentDateChanged,
      @Param("federal_state") int federalState,
      @Param("administrative_unit") int administrativeUnit,
      @Param("age_group") int ageGroup,
      @Param("submitted_at") LocalDate submittedAt);
}
