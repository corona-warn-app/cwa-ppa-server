package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;

public class ClientMetadata extends DataDonationMetric {

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadataDetails;

  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  public ClientMetadata(Long id, ClientMetadataDetails clientMetadataDetails,
      TechnicalMetadata technicalMetadata) {
    super(id);
    this.clientMetadataDetails = clientMetadataDetails;
    this.technicalMetadata = technicalMetadata;
  }

  public ClientMetadataDetails getClientMetadataDetails() {
    return clientMetadataDetails;
  }

  public TechnicalMetadata getTechnicalMetadata() {
    return technicalMetadata;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, clientMetadataDetails, technicalMetadata);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ClientMetadata other = (ClientMetadata) obj;
    if (clientMetadataDetails == null) {
      if (other.clientMetadataDetails != null) {
        return false;
      }
    } else if (!clientMetadataDetails.equals(other.clientMetadataDetails)) {
      return false;
    }
    if (technicalMetadata == null) {
      if (other.technicalMetadata != null) {
        return false;
      }
    } else if (!technicalMetadata.equals(other.technicalMetadata)) {
      return false;
    }
    return true;
  }
}
