package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
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
  @Query("INSERT INTO android_id (id, expiration_date, last_used_srs) VALUES (:id, :expirationDate, :lastUsedSRS)")
  void insert(@Param("id") final String id,
      @Param("expirationDate") final Long expirationDate,
      @Param("lastUsedSRS") final Long lastUsedSrs);

  @Modifying
  @Query("UPDATE android_id SET expiration_date = :expirationDate, last_used_srs = :lastUsedSRS WHERE id = :id")
  void update(@Param("id") final String id,
      @Param("expirationDate") final Long expirationDate,
      @Param("lastUsedSRS") final Long lastUsedSrs);

  @Query("SELECT COUNT(*) FROM android_id WHERE expiration_date < :threshold")
  int countOlderThan(@Param("threshold") long threshold);

  @Modifying
  @Query("DELETE FROM android_id WHERE expiration_date < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);
}
