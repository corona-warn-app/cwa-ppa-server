package app.coronawarn.datadonation.common.persistence.domain.metrics;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;

/**
 * Acts as the base class with common features for entities that capture Data Donation information.
 */
public abstract class DataDonationMetric {

  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  @Id
  protected final Long id;

  protected DataDonationMetric(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  /**
   * Performs a validation of constraints defined in subclasses.
   *
   * @return A set of constraint violations of this entity.
   */
  public Set<ConstraintViolation<DataDonationMetric>> validate() {
    return VALIDATOR.validate(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    DataDonationMetric other = (DataDonationMetric) obj;
    return Objects.equals(id, other.id);
  }
}
