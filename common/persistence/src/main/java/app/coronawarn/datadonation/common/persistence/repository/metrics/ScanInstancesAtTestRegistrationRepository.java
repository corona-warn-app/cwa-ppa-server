package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstancesAtTestRegistration;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ScanInstancesAtTestRegistrationRepository extends
    CrudRepository<ScanInstancesAtTestRegistration, Long> {

  @Query("select count(*) from scan_instances_at_test_registration where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from scan_instances_at_test_registration where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);
}
