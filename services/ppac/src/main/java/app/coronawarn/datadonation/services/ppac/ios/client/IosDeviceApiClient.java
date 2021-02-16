package app.coronawarn.datadonation.services.ppac.ios.client;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface IosDeviceApiClient {

  ResponseEntity<String> queryDeviceData(final String jwt,
      PerDeviceDataQueryRequest queryRequest);

  ResponseEntity<Void> updatePerDeviceData(final String jwt,
      PerDeviceDataUpdateRequest updateRequest);
}
