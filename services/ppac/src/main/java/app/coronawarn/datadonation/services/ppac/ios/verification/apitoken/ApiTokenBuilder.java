package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;

public class ApiTokenBuilder {

  private String apiToken;
  private Long lastUsedPpac;
  private Long lastUsedEdus;
  private Long expirationDate;
  private Long createdAt;

  public ApiTokenBuilder setApiToken(String apiToken) {
    this.apiToken = apiToken;
    return this;
  }

  public ApiTokenBuilder setLastUsedPpac(Long lastUsedPpac) {
    this.lastUsedPpac = lastUsedPpac;
    return this;
  }

  public ApiTokenBuilder setLastUsedEdus(Long lastUsedEdus) {
    this.lastUsedEdus = lastUsedEdus;
    return this;
  }

  public ApiTokenBuilder setExpirationDate(Long expirationDate) {
    this.expirationDate = expirationDate;
    return this;
  }

  public ApiTokenBuilder setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public static ApiTokenBuilder newBuilder() {
    return new ApiTokenBuilder();
  }

  public ApiToken build() {
    return new ApiToken(apiToken, expirationDate, createdAt, lastUsedEdus, lastUsedPpac);
  }
}
