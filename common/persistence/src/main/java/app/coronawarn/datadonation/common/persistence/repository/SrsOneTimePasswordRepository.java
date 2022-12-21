package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.SrsOneTimePassword;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <code>created_at</code> time in <strong>seconds</strong> since epoch.
 */
@Repository
public interface SrsOneTimePasswordRepository extends CrudRepository<SrsOneTimePassword, String> {

  @Query("SELECT COUNT(*) FROM srs_one_time_password WHERE expiration_timestamp < :threshold "
      + "OR redemption_timestamp < :threshold")
  int countOlderThan(@Param("threshold") long threshold);

  @Modifying
  @Query("DELETE FROM srs_one_time_password WHERE expiration_timestamp < :threshold "
      + "OR redemption_timestamp < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);

  @Modifying
  @Query("INSERT INTO srs_one_time_password (password, redemption_timestamp, expiration_timestamp) "
      + "VALUES(:password, :redemptionTimestamp, :expirationTimestamp)")
  void insert(@Param("password") String password,
      @Param("redemptionTimestamp") Long redemptionTimestamp,
      @Param("expirationTimestamp") Long expirationTimestamp);
}
