package app.coronawarn.analytics.services.ios.control;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApiTokenService {


    private final ApiTokenRepository apiTokenRepository;

    public ApiTokenService(ApiTokenRepository apiTokenRepository) {
        this.apiTokenRepository = apiTokenRepository;
    }

    public Optional<ApiToken> retrieveApiToken(String apiToken) {
        return Optional.empty();
    }

    public ApiToken create(ApiToken apiToken) {
        return apiTokenRepository.save(apiToken);
    }
}
