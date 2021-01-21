package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import java.time.LocalDate;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiTokenRepository extends CrudRepository<ApiToken, String> {

  @Modifying
  @Query("insert into api_token (api_token,expiration_date,last_used_edus, last_used_ppac)"
      + "values(:apiToken,:expirationDate,:lastUsedEDUS,:lastUsedPPAC)")
  void insert(@Param("apiToken") String apiToken,
      @Param("expirationDate") LocalDate expirationDate,
      @Param("lastUsedEDUS") Long lastUsedEdus,
      @Param("lastUsedPPAC") Long lastUsedPpac);

}
