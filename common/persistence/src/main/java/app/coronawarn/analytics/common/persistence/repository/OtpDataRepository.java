package app.coronawarn.analytics.common.persistence.repository;

import app.coronawarn.analytics.common.persistence.domain.OtpData;
import org.springframework.data.repository.CrudRepository;

public interface OtpDataRepository extends CrudRepository<OtpData, String> {
}
