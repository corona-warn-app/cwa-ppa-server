package app.coronawarn.analytics.common.persistence.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

// spring data jdbc
public class ApiToken {

    @Id
    private String apiToken;

    private LocalDateTime expirationDate;

    @Column("last_used_edus")
    private LocalDate lastUsedEDUS;

    @Column("last_used_ppac")
    private LocalDate lastUsedPPAC;


    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getLastUsedEDUS() {
        return lastUsedEDUS;
    }

    public void setLastUsedEDUS(LocalDate lastUsedEDUS) {
        this.lastUsedEDUS = lastUsedEDUS;
    }

    public LocalDate getLastUsedPPAC() {
        return lastUsedPPAC;
    }

    public void setLastUsedPPAC(LocalDate lastUsedPPAC) {
        this.lastUsedPPAC = lastUsedPPAC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiToken apiToken1 = (ApiToken) o;
        return Objects.equals(apiToken, apiToken1.apiToken) &&
                Objects.equals(expirationDate, apiToken1.expirationDate) &&
                Objects.equals(lastUsedEDUS, apiToken1.lastUsedEDUS) &&
                Objects.equals(lastUsedPPAC, apiToken1.lastUsedPPAC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiToken, expirationDate, lastUsedEDUS, lastUsedPPAC);
    }
}
