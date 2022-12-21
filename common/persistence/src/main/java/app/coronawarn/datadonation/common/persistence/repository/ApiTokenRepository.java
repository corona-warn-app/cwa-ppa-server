package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <code>created_at</code> time in <strong>seconds</strong> since epoch.
 */
@Repository
public interface ApiTokenRepository extends CrudRepository<ApiTokenData, String> {

  @Modifying
  @Query("INSERT INTO api_token (api_token, expiration_date, created_at, last_used_edus, last_used_ppac, last_used_srs)"
      + "VALUES(:apiToken, :expirationDate, :createdAt, :lastUsedEDUS, :lastUsedPPAC, :lastUsedSRS)")
  void insert(@Param("apiToken") String apiToken,
      @Param("expirationDate") Long expirationDate,
      @Param("createdAt") Long createdAt,
      @Param("lastUsedEDUS") Long lastUsedEdus,
      @Param("lastUsedPPAC") Long lastUsedPpac,
      @Param("lastUsedSRS") Long lastUsedSrs);

  @Modifying
  @Query("DELETE FROM api_token WHERE last_used_srs IS NULL AND expiration_date < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);

  @Modifying
  @Query("DELETE FROM api_token WHERE expiration_date < :threshold")
  void deleteSrsOlderThan(@Param("threshold") long threshold);

  @Query("SELECT COUNT(*) FROM api_token WHERE expiration_date < :threshold")
  int countOlderThan(@Param("threshold") long threshold);
}
