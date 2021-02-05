package app.coronawarn.datadonation.common.persistence.repository.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureWindow;
import org.springframework.data.repository.CrudRepository;

public interface ExposureWindowRepository extends CrudRepository<ExposureWindow, Long> {
}
