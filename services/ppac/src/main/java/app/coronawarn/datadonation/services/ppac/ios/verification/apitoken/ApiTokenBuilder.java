package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken;

import app.coronawarn.datadonation.common.persistence.domain.ApiTokenData;

public class ApiTokenBuilder {

  public static ApiTokenBuilder newBuilder() {
    return new ApiTokenBuilder();
  }

  private String apiToken;
  private Long createdAt;
  private Long expirationDate;
  private Long lastUsedEdus;
  private Long lastUsedPpac;
  private Long lastUsedSrs;

  public ApiTokenData build() {
    return new ApiTokenData(apiToken, expirationDate, createdAt, lastUsedEdus, lastUsedPpac, lastUsedSrs);
  }

  public ApiTokenBuilder setApiToken(final String apiToken) {
    this.apiToken = apiToken;
    return this;
  }

  public ApiTokenBuilder setCreatedAt(final Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public ApiTokenBuilder setExpirationDate(final Long expirationDate) {
    this.expirationDate = expirationDate;
    return this;
  }

  public ApiTokenBuilder setLastUsedEdus(final Long lastUsedEdus) {
    this.lastUsedEdus = lastUsedEdus;
    return this;
  }

  public ApiTokenBuilder setLastUsedPpac(final Long lastUsedPpac) {
    this.lastUsedPpac = lastUsedPpac;
    return this;
  }

  public ApiTokenBuilder setLastUsedSrs(final Long lastUsedSrs) {
    this.lastUsedSrs = lastUsedSrs;
    return this;
  }
}
