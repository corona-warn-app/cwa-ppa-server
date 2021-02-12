package app.coronawarn.datadonation.common.persistence.repository.metrics;

import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import app.coronawarn.datadonation.common.persistence.domain.metrics.UserMetadata;

public interface UserMetadataRepository extends CrudRepository<UserMetadata, Long> {

  @Query("select count(*) from user_metadata where submitted_at < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

  @Modifying
  @Query("delete from user_metadata where submitted_at < :threshold")
  void deleteOlderThan(@Param("threshold") LocalDate threshold);
}
