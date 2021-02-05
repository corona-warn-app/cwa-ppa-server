package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExposureWindowRepository extends CrudRepository<ExposureWindow, Long> {

  @Query("select count(*) from exposure_window where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from exposure_window where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("INSERT INTO exposure_window (id, date, report_type, "
      + "infectiousness, callibration_confidence, transmission_risk_level, normalized_time, "
      + "cwa_version_major, cwa_version_minor, cwa_version_patch, app_config_etag, submitted_at)"
      + "VALUES (:id, :date, :report_type, :infectiousness, :callibration_confidence, "
      + ":transmission_risk_level, :normalized_time, :cwa_version_major, :cwa_version_minor, "
      + ":cwa_version_patch, :app_config_etag, :submitted_at)")
  void persist(
      @Param("id") Long id,
      @Param("date") LocalDate date,
      @Param("report_type") int reportType,
      @Param("infectiousness") int infectiousness,
      @Param("callibration_confidence") int callibrationConfidence,
      @Param("transmission_risk_level") int transmissionRiskLevel,
      @Param("normalized_time") long normalizedTime,
      @Param("cwa_version_major") int cwaVersionMajor,
      @Param("cwa_version_minor") int cwaVersionMinor,
      @Param("cwa_version_patch") int cwaVersionPatch,
      @Param("app_config_etag") String appConfigEtag,
      @Param("submitted_at") LocalDate submittedAt);
}
