package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface ExposureWindowRepository extends CrudRepository<ExposureWindow, Long> {

  @Query("select count(*) from exposure_window where date < :threshold")
  int countOlderThan(LocalDate threshold);

  @Modifying
  @Query("delete from exposure_window where date < :threshold")
  void deleteOlderThan(LocalDate threshold);
}
