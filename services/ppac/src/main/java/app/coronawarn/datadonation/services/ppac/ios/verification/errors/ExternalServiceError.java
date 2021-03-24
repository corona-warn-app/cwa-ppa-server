package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

import feign.FeignException;

public class ExternalServiceError extends InternalServerError {

  public ExternalServiceError(FeignException cause) {
    super("Exception contacting external service occurred with status " + cause.status() + " and response "
        + cause.contentUTF8() + " to request " + cause.request(), cause);
  }
}
