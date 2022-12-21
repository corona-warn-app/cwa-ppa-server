package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AndroidIdUpsertError extends RuntimeException {

  private static final long serialVersionUID = 6883040825312105258L;

  public AndroidIdUpsertError() {
    super("Android ID could not be persisted");
  }
}
