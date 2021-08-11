package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ScanInstance;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ScanInstanceRepository extends CrudRepository<ScanInstance, Long> {

  @Modifying
  @Query("delete from scan_instance where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);
}
