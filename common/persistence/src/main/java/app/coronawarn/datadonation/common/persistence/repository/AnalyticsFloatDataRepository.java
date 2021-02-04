package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.AnalyticsFloatData;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsFloatDataRepository extends CrudRepository<AnalyticsFloatData, Long> {

  @Modifying
  @Query("delete from float_data where m_day < :threshold")
  void deleteOlderThan(LocalDate threshold);

  @Modifying
  @Query("select count(*) from float_data where m_day < :threshold")
  int countOlderThan(@Param("threshold") LocalDate threshold);

}
