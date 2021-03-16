package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <code>created_at</code> time in <strong>seconds</strong> since epoch.
 */
@Repository
public interface ApiTokenRepository extends CrudRepository<ApiToken, String> {

  @Modifying
  @Query("insert into api_token (api_token,expiration_date,created_at,last_used_edus, last_used_ppac)"
      + "values(:apiToken,:expirationDate,:createdAt,:lastUsedEDUS,:lastUsedPPAC)")
  void insert(@Param("apiToken") String apiToken,
      @Param("expirationDate") Long expirationDate,
      @Param("createdAt") Long createdAt,
      @Param("lastUsedEDUS") Long lastUsedEdus,
      @Param("lastUsedPPAC") Long lastUsedPpac);

  @Modifying
  @Query("delete from api_token where created_at < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);

  @Query("select count(*) from api_token where created_at < :threshold")
  int countOlderThan(@Param("threshold") long threshold);
}
