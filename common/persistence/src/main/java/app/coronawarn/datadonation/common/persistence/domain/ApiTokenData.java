package app.coronawarn.datadonation.common.persistence.domain;

import java.util.Objects;
import java.util.Optional;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("api_token")
public class ApiTokenData {

  @Id
  private String apiToken;
  private Long expirationDate;
  private Long createdAt;
  private Long lastUsedEdus;
  private Long lastUsedPpac;
  private Long lastUsedSrs;

  /**
   * Create new instance of apitoken.
   *
   * @param apiToken       the api token string as unique identifier.
   * @param expirationDate when the apitoken will be expired.
   * @param createdAt      when the apitoken was created.
   * @param lastUsedEdus   when the apitoken was last used in an otp scenario.
   * @param lastUsedPpac   when the apitoken was last used in an data scenario.
   * @param lastUsedSrs    when the apitoken was last used in a data scenario.
   */
  public ApiTokenData(String apiToken, Long expirationDate, Long createdAt, Long lastUsedEdus, Long lastUsedPpac, Long lastUsedSrs) {
    this.apiToken = apiToken;
    this.expirationDate = expirationDate;
    this.createdAt = createdAt;
    this.lastUsedEdus = lastUsedEdus;
    this.lastUsedPpac = lastUsedPpac;
    this.lastUsedSrs = lastUsedSrs;
  }

  public Long getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Long expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  public Optional<Long> getLastUsedEdus() {
    return Optional.ofNullable(lastUsedEdus);
  }

  public void setLastUsedEdus(Long lastUsedEdus) {
    this.lastUsedEdus = lastUsedEdus;
  }

  public Optional<Long> getLastUsedPpac() {
    return Optional.ofNullable(lastUsedPpac);
  }

  public void setLastUsedPpac(Long lastUsedPpac) {
    this.lastUsedPpac = lastUsedPpac;
  }

  public Optional<Long> getLastUsedSrs() {
    return Optional.ofNullable(lastUsedSrs);
  }

  public void setLastUsedSrs(Long lastUsedSrs) {
    this.lastUsedSrs = lastUsedSrs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiTokenData apiTokenData1 = (ApiTokenData) o;
    return Objects.equals(apiToken, apiTokenData1.apiToken)
        && Objects.equals(expirationDate, apiTokenData1.expirationDate)
        && Objects.equals(lastUsedEdus, apiTokenData1.lastUsedEdus)
        && Objects.equals(lastUsedPpac, apiTokenData1.lastUsedPpac)
        && Objects.equals(lastUsedSrs, apiTokenData1.lastUsedSrs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiToken, expirationDate, lastUsedEdus, lastUsedPpac, lastUsedSrs);
  }
}
