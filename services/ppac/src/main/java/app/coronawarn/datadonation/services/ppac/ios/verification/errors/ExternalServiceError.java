package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

import feign.FeignException;

public class ExternalServiceError extends InternalServerError {

  private static final long serialVersionUID = -1420803245315169479L;

  public ExternalServiceError(FeignException cause) {
    super("Responded with HTTP-" + cause.status() + ": " + cause.getMessage(), cause);
  }
}
