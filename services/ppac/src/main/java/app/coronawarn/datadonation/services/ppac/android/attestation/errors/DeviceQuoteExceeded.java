package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DeviceQuoteExceeded extends RuntimeException {

  private static final long serialVersionUID = 8228950515673394141L;

  public DeviceQuoteExceeded() {
    super("Device quota exceeded");
  }
}
