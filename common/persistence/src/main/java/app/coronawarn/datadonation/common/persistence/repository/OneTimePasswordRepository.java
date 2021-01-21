package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.springframework.data.repository.CrudRepository;

public interface OneTimePasswordRepository extends CrudRepository<OneTimePassword, String> {
}
