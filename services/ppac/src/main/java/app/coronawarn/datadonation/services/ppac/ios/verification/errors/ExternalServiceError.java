package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

import feign.FeignException;

@SuppressWarnings("serial")
public class ExternalServiceError extends InternalServerError {

  public ExternalServiceError(FeignException cause) {
    super("'" + cause.request() + "' responded with HTTP-" + cause.status() + ": '" + cause.contentUTF8() + "'", cause);
  }
}
