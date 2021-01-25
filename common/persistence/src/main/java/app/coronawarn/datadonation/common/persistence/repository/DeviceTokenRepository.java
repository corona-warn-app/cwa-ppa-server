package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends CrudRepository<DeviceToken, String> {

  @Modifying
  @Query("INSERT INTO device_token(device_token_hash, created_at)"
      + "VALUES(:deviceTokenHash, :createdAt)")
  boolean save(@Param("deviceTokenHash") String deviceTokenHash, @Param("createdAt") Long createdAt);

}
