package app.coronawarn.analytics.common.persistence.domain;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

public class ApiToken {

  @Id
  private String apiToken;
  private LocalDate expirationDate;
  private Long lastUsedEdus;
  private Long lastUsedPpac;


  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Long getLastUsedEdus() {
    return lastUsedEdus;
  }

  public void setLastUsedEdus(Long lastUsedEdus) {
    this.lastUsedEdus = lastUsedEdus;
  }

  public Long getLastUsedPpac() {
    return lastUsedPpac;
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
