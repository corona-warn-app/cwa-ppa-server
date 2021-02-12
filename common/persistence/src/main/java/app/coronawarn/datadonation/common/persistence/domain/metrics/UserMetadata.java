package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.UserMetadataDetails;

public class UserMetadata extends DataDonationMetric {

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final UserMetadataDetails userMetadataDetails;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance
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
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    UserMetadata other = (UserMetadata) obj;
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    if (userMetadataDetails == null) {
      if (other.userMetadataDetails != null) {
        return false;
      }
    } else if (!userMetadataDetails.equals(other.userMetadataDetails))
      return false;
    return true;
  }
}
