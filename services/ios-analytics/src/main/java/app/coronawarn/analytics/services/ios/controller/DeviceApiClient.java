package app.coronawarn.analytics.services.ios.controller;

import app.coronawarn.analytics.services.ios.domain.DeviceData;
import app.coronawarn.analytics.services.ios.domain.DeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.domain.DeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.domain.DeviceValidationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "deviceApi", url = "${ppac.device-identification-url}")
public interface DeviceApiClient {

  @PostMapping(value = "query_two_bits",
      headers = {"Authorization=Bearer ${ios.deviceapi.token}"})
  DeviceData queryDeviceData(DeviceDataQueryRequest queryRequest);

  @PostMapping(value = "update_two_bits",
      headers = {"Authorization=Bearer ${ios.deviceapi.token}"})
  void updatePerDeviceData(DeviceDataUpdateRequest updateRequest);

  @PostMapping(value = "validate_device_token",
      headers = {"Authorization=Bearer ${ios.deviceapi.token}"})
  void validateDevice(DeviceValidationRequest deviceValidationRequest);


}
