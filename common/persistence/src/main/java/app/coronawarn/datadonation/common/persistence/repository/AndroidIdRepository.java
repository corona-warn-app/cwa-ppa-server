package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
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
public interface AndroidIdRepository extends CrudRepository<AndroidId, String> {

  @Modifying
  @Query("insert into android_id (id, expiration_date, last_used_for_srs)"
      + "values(:id,:expirationDate,:lastUsedSRS)")
  void insert(@Param("id") String id,
      @Param("expirationDate") Long expirationDate,
      @Param("lastUsedSRS") Long lastUsedSrs);

  @Modifying
  @Query("update android_id (id, expiration_date, last_used_for_srs)"
          + "values(:id,:expirationDate,:lastUsedSRS)")
  void update(@Param("id") String id,
              @Param("expirationDate") Long expirationDate,
              @Param("lastUsedSRS") Long lastUsedSrs);

  @Query("select count(*) from android_id where expiration_date < :threshold")
  int countOlderThan(@Param("threshold") long threshold);
}
