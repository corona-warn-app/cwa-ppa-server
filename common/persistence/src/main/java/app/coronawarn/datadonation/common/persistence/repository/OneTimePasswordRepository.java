package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OneTimePasswordRepository extends CrudRepository<OneTimePassword, String> {

  @Modifying
  @Query("delete from one_time_password where creationTimestamp < :threshold")
  void deleteOlderThan(long threshold);

  @Modifying
  @Query("select count(*) from one_time_password where creationTimestamp < :threshold")
  int countOlderThan(@Param("threshold") long threshold);

}
