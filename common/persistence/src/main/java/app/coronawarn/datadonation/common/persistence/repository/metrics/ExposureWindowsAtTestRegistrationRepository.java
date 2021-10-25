package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindowsAtTestRegistration;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface ExposureWindowsAtTestRegistrationRepository extends CrudRepository<ExposureWindowsAtTestRegistration, Long> {

  @Query("select count(*) from exposure_windows_at_test_registration where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from exposure_windows_at_test_registration where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);

}
