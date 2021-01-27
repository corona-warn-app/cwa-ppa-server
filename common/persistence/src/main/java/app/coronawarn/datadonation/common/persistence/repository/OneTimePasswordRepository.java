package app.coronawarn.datadonation.common.persistence.repository;

import app.coronawarn.datadonation.common.persistence.domain.OneTimePassword;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OneTimePasswordRepository extends CrudRepository<OneTimePassword, String> {

}
