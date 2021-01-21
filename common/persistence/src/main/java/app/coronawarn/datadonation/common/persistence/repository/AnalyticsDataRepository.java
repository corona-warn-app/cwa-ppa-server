package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.AnalyticsIntData;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsDataRepository extends CrudRepository<AnalyticsIntData, Long> {

  @Modifying
  @Query("INSERT INTO data " + "(os, key, value) " + "VALUES (:os, :key, :value)")
  boolean save(@Param("os") int os, @Param("key") int key, @Param("value") Long value);
}
