package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.ElsOneTimePassword;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ElsOneTimePasswordRepository extends CrudRepository<ElsOneTimePassword, String> {

  @Modifying
  @Query("delete from els_one_time_password where expiration_timestamp < :threshold "
      + "or redemption_timestamp < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);

  @Query("select count(*) from els_one_time_password where expiration_timestamp < :threshold "
      + "or redemption_timestamp < :threshold")
  int countOlderThan(@Param("threshold") long threshold);

  @Modifying
  @Query("insert into els_one_time_password (password, redemption_timestamp, expiration_timestamp) "
      + "values(:password, :redemptionTimestamp, :expirationTimestamp)")
  void insert(@Param("password") String password,
      @Param("redemptionTimestamp") Long redemptionTimestamp,
      @Param("expirationTimestamp") Long expirationTimestamp
  );
}
