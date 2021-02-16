package app.coronawarn.datadonation.services.ppac.ios.client;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestIosDeviceApiClient implements IosDeviceApiClient {

  @Override
  public ResponseEntity<String> queryDeviceData(String jwt, PerDeviceDataQueryRequest queryRequest) {
    return ResponseEntity.ok("success");
  }

  @Override
  public ResponseEntity<Void> updatePerDeviceData(String jwt, PerDeviceDataUpdateRequest updateRequest) {
    return ResponseEntity.ok().build();
  }
}
