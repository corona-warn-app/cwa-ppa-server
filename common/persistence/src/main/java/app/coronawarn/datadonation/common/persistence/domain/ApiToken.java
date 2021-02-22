package app.coronawarn.datadonation.common.persistence.domain;

import java.util.Objects;
import java.util.Optional;
import org.springframework.data.annotation.Id;

public class ApiToken {

  @Id
  private String apiToken;
  private Long expirationDate;
  private Long createdAt;
  private Long lastUsedEdus;
  private Long lastUsedPpac;

  /**
   * Create new instance of apitoken.
   *
   * @param apiToken       the api token string as unique identifier.
   * @param expirationDate when the apitoken will be expired.
   * @param createdAt      when the apitoken was created.
   * @param lastUsedEdus   when the apitoken was last used in an otp scenario.
   * @param lastUsedPpac   when the apitoken was last used in an data scenario.
   */
  public ApiToken(String apiToken, Long expirationDate, Long createdAt, Long lastUsedEdus, Long lastUsedPpac) {
    this.apiToken = apiToken;
    this.expirationDate = expirationDate;
    this.createdAt = createdAt;
    this.lastUsedEdus = lastUsedEdus;
    this.lastUsedPpac = lastUsedPpac;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiToken apiToken1 = (ApiToken) o;
    return Objects.equals(apiToken, apiToken1.apiToken)
        && Objects.equals(expirationDate, apiToken1.expirationDate)
        && Objects.equals(lastUsedEdus, apiToken1.lastUsedEdus)
        && Objects.equals(lastUsedPpac, apiToken1.lastUsedPpac);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiToken, expirationDate, lastUsedEdus, lastUsedPpac);
  }
}
