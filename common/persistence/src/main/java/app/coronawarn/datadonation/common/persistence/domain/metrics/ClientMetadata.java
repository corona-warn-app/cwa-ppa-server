package app.coronawarn.datadonation.common.persistence.domain.metrics;

import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

public class ClientMetadata extends DataDonationMetric {

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final ClientMetadataDetails clientMetadataDetails;

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private final TechnicalMetadata technicalMetadata;

  /**
   * Constructs an immutable instance.
   */
  public ClientMetadata(Long id, ClientMetadataDetails clientMetadataDetails, TechnicalMetadata technicalMetadata) {
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
    if (!super.equals(obj)) {
      return false;
    }
    ClientMetadata other = (ClientMetadata) obj;
    return Objects.equals(clientMetadataDetails, other.clientMetadataDetails)
        && Objects.equals(technicalMetadata, other.technicalMetadata);
  }
}
