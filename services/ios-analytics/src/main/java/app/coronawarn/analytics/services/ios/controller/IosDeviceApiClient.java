package app.coronawarn.analytics.services.ios.controller;

import app.coronawarn.analytics.services.ios.domain.IosDeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceValidationRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// Each request must include an authorization header that contains your auth key. Must use ES256 and be base64-url encoded
@FeignClient(name = "deviceApi", url = "${ppac.device-identification-url}")
// For testing https://developer.apple.com/documentation/devicecheck/accessing_and_modifying_per-device_data use "https://api.development.devicecheck.apple.com"
public interface IosDeviceApiClient {

  @PostMapping(value = "query_two_bits",
      headers = {"Authorization=Bearer ${ios.deviceapi.token}"})
  IosDeviceData queryDeviceData(IosDeviceDataQueryRequest queryRequest);

  @PostMapping(value = "update_two_bits",
      headers = {"Authorization=Bearer ${ios.deviceapi.token}"})
  void updatePerDeviceData(IosDeviceDataUpdateRequest updateRequest);

  @PostMapping(value = "validate_device_token",
      headers = {"Authorization=Bearer ${ios.deviceapi.token}"})
  void validateDevice(IosDeviceValidationRequest iosDeviceValidationRequest);


}
