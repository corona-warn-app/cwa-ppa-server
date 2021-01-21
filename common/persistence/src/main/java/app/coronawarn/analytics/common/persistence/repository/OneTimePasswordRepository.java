package app.coronawarn.analytics.common.persistence.repository;

import app.coronawarn.analytics.common.persistence.domain.OneTimePassword;
import org.springframework.data.repository.CrudRepository;

public interface OneTimePasswordRepository extends CrudRepository<OneTimePassword, String> {
}
