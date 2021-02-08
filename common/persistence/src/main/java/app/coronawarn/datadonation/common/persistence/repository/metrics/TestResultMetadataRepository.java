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
}
