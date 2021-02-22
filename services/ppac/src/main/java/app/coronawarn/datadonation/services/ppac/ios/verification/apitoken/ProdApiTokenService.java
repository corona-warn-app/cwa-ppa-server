package app.coronawarn.datadonation.services.ppac.ios.verification.apitoken;

import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.verification.JwtProvider;
import app.coronawarn.datadonation.services.ppac.ios.verification.PpacIosScenarioRepository;
import app.coronawarn.datadonation.services.ppac.ios.verification.apitoken.authentication.ApiTokenAuthenticationStrategy;
import app.coronawarn.datadonation.services.ppac.ios.verification.errors.InternalError;
import app.coronawarn.datadonation.services.ppac.ios.verification.scenario.ratelimit.PpacIosRateLimitStrategy;
import feign.FeignException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!loadtest")
public class ProdApiTokenService extends ApiTokenService {

  public ProdApiTokenService(ApiTokenRepository apiTokenRepository,
      IosDeviceApiClient iosDeviceApiClient, JwtProvider jwtProvider,
      ApiTokenAuthenticationStrategy apiTokenAuthenticationStrategy,
      PpacIosRateLimitStrategy iosScenarioValidator,
      PpacIosScenarioRepository ppacIosScenarioRepository) {
    super(apiTokenRepository, iosDeviceApiClient, jwtProvider, apiTokenAuthenticationStrategy,
        iosScenarioValidator, ppacIosScenarioRepository);
  }

  @Override
  protected void treatApiClientErrors(FeignException e) {
    throw new InternalError(e);
  }
}
