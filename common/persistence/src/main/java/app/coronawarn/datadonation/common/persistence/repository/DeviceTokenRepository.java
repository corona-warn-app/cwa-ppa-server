package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.DeviceToken;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTokenRepository extends CrudRepository<DeviceToken, Long> {

  @Query("SELECT * FROM device_token d WHERE d.device_token_hash = :deviceTokenHash ")
  Optional<DeviceToken> findByDeviceTokenHash(@Param("deviceTokenHash") byte[] deviceToken);
}
