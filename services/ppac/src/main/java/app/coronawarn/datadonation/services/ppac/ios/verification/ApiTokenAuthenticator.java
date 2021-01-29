package app.coronawarn.datadonation.services.ppac.ios.verification;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;

public interface ApiTokenAuthenticator {

  void checkApiTokenAlreadyIssued(PerDeviceDataResponse perDeviceDataResponseOptional,
      boolean ignoreApiTokenAlreadyIssued);
}
