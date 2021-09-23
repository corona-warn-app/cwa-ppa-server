package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

import feign.FeignException;

@SuppressWarnings("serial")
public class ExternalServiceError extends InternalServerError {

  public ExternalServiceError(FeignException cause) {
    super("Responded with HTTP-" + cause.status() + ": " + cause.getMessage(), cause);
  }
}
