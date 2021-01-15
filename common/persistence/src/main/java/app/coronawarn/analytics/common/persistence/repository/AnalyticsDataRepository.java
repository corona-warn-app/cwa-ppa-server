package app.coronawarn.analytics.common.persistence.repository;

import app.coronawarn.analytics.common.persistence.domain.AnalyticsData;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsDataRepository extends CrudRepository<AnalyticsData, Long> {

  @Modifying
  @Query("INSERT INTO data "
      + "(key, value) "
      + "VALUES (:key, :value) "
      + "ON CONFLICT DO NOTHING")
  boolean saveDoNothingOnConflict(
      @Param("key")  int key,
      @Param("value") int value
      );
}
