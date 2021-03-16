package app.coronawarn.datadonation.common.persistence.repository.ppac.android;

import app.coronawarn.datadonation.common.persistence.domain.ppac.android.Salt;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <code>created_at</code> time in <strong>milliseconds</strong> since epoch.
 */
@Repository
public interface SaltRepository extends CrudRepository<Salt, String> {

  @Modifying
  @Query("insert into salt (salt,created_at)" + "values(:salt,:createdAt)")
  void persist(@Param("salt") String salt, @Param("createdAt") long createdAt);

  @Modifying
  @Query("delete from salt where created_at < :threshold")
  void deleteOlderThan(@Param("threshold") long threshold);

  @Query("select count(*) from salt where created_at < :threshold")
  int countOlderThan(@Param("threshold") long threshold);
}
