package app.coronawarn.analytics.common.persistence.repository;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface ApiTokenRepository extends CrudRepository<ApiToken, String> {
    
    @Modifying
    @Query("insert into api_token (api_token,expiration_date,last_used_edus, last_used_ppac)" +
            "values(:apiToken,:expirationDate,:lastUsedEDUS,:lastUsedPPAC)")
    void insert(@Param("apiToken") String apiToken,
                @Param("expirationDate") LocalDateTime expirationDate,
                @Param("lastUsedEDUS") Long lastUsedEDUS,
                @Param("lastUsedPPAC") Long lastUsedPPAC);

}
