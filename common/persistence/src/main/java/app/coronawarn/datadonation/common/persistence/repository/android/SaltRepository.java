package app.coronawarn.datadonation.common.persistence.repository.android;

import app.coronawarn.datadonation.common.persistence.domain.android.Salt;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaltRepository extends CrudRepository<Salt, String> {

  @Modifying
  @Query("insert into salt (salt,created_at)" + "values(:salt,:createdAt)")
  void persist(@Param("salt") String salt, @Param("createdAt") long createdAt);
}
