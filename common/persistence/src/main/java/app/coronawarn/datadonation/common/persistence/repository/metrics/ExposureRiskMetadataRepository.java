package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;

public interface ExposureRiskMetadataRepository
    extends CrudRepository<ExposureRiskMetadata, Long> {

  @Query("select count(*) from exposure_risk_metadata where submitted_at < :threshold")
  int countOlderThan(LocalDate threshold);

  @Modifying
  @Query("delete from exposure_risk_metadata where submitted_at < :threshold")
  void deleteOlderThan(LocalDate threshold);
}
