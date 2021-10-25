package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.SummarizedExposureWindowsWithUserMetadata;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface SummarizedExposureWindowsWithUserMetadataRepository extends CrudRepository<SummarizedExposureWindowsWithUserMetadata, Long> {

  @Query("select count(*) from summarized_exposure_windows_with_user_metadata where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from summarized_exposure_windows_with_user_metadata where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);

}
