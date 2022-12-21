package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DeviceQuotaExceeded extends RuntimeException {

  private static final long serialVersionUID = 8228950515673394141L;

  public DeviceQuotaExceeded() {
    super("Device quota exceeded");
  }
}
