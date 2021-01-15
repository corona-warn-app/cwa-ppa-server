package app.coronawarn.analytics.common.persistence.repository;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiTokenRepository extends CrudRepository<ApiToken, Long> {
}
