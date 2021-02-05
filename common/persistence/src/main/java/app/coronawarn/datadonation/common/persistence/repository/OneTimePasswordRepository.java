package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OneTimePasswordRepository extends CrudRepository<OneTimePassword, String> {

  @Modifying
  @Query("delete from one_time_password where creation_timestamp < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);

  @Query("select count(*) from one_time_password where creation_timestamp < :threshold")
  int countOlderThan(@Param("threshold") long threshold);

  @Modifying
  @Query("insert into one_time_password (password, creation_timestamp, redemption_timestamp, "
      + "last_validity_check_timestamp) values(:password, :creationTimestamp, :redemptionTimestamp, "
      + ":lastValidityCheckTimestamp)")
  void insert(@Param("password") String password, @Param("creationTimestamp") Long creationTimestamp,
      @Param("redemptionTimestamp") Long redemptionTimestamp,
      @Param("lastValidityCheckTimestamp") Long lastValidityCheckTimestamp);
}
