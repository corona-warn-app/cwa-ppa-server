package app.coronawarn.analytics.services.ios.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import app.coronawarn.analytics.services.ios.domain.DeviceData;
import app.coronawarn.analytics.services.ios.domain.DeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.domain.DeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.domain.DeviceValidationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "deviceApi", url = "${ppac.device-identification-url}")
public interface DeviceApiClient {

  @PostMapping(value = "query_two_bits")
  DeviceData queryDeviceData(@RequestHeader(AUTHORIZATION) final String jwt, DeviceDataQueryRequest queryRequest);

  @PostMapping(value = "update_two_bits")
  void updatePerDeviceData(@RequestHeader(AUTHORIZATION) final String jwt, DeviceDataUpdateRequest updateRequest);

  @PostMapping(value = "validate_device_token")
  void validateDevice(@RequestHeader(AUTHORIZATION) final String jwt, DeviceValidationRequest deviceValidationRequest);
}
