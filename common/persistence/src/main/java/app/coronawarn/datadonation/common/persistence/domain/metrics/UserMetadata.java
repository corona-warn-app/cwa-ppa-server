package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class UserMetadata extends DataDonationMetric {

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadataDetails;

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public UserMetadata(Long id, UserMetadataDetails userMetadataDetails,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.userMetadataDetails = userMetadataDetails;
    this.technicalMetadata = technicalMetadata;
  }

  public UserMetadataDetails getUserMetadataDetails() {
    return userMetadataDetails;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userMetadataDetails, technicalMetadata);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    UserMetadata that = (UserMetadata) obj;
    return Objects.equals(userMetadataDetails, that.userMetadataDetails)
        && Objects.equals(technicalMetadata, that.technicalMetadata);
  }
}
