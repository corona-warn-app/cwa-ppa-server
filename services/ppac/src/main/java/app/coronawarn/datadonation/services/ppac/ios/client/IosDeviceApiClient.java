package app.coronawarn.datadonation.services.ppac.ios.client;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataQueryRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceValidationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "deviceApi", url = "${ppac.ios.device-api-url}")
public interface IosDeviceApiClient {

  @PostMapping(value = "/query_two_bits")
  ResponseEntity<String> queryDeviceData(@RequestHeader(AUTHORIZATION) final String jwt,
      @RequestBody PerDeviceDataQueryRequest queryRequest);

  @PostMapping(value = "update_two_bits")
  void updatePerDeviceData(@RequestHeader(AUTHORIZATION) final String jwt,
      PerDeviceDataUpdateRequest updateRequest);

  @PostMapping(value = "validate_device_token")
  void validateDevice(@RequestHeader(AUTHORIZATION) final String jwt,
      PerDeviceValidationRequest perDeviceValidationRequest);
}
