package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OneTimePasswordRepository extends CrudRepository<OneTimePassword, String> {

  @Modifying
  @Query("insert into one_time_password (password, redemption_timestamp) "
      + "values(:password, :redemptionTimestamp)")
  void insert(@Param("password") String password,
      @Param("redemptionTimestamp") Long redemptionTimestamp);
}
