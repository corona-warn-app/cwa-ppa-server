package app.coronawarn.analytics.common.persistence.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class ApiToken {

    @Id
    Long id;

    @Column("api_token")
    String apiToken;

    @Column("expiration_date")
    OffsetDateTime expirationDate;

    @Column("last_used")
    OffsetDateTime lastUsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public OffsetDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(OffsetDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public OffsetDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(OffsetDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }
}
