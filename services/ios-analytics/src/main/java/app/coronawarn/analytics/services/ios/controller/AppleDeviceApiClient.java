package app.coronawarn.analytics.services.ios.controller;

import app.coronawarn.analytics.services.ios.domain.IosDeviceDataQueryRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataUpdateRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceValidationRequest;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// Each request must include an authorization header that cotains your auth key. Must use ES256 and be base64-url encoded
@FeignClient(name = "https://api.development.devicecheck.apple.com")
// For testing https://developer.apple.com/documentation/devicecheck/accessing_and_modifying_per-device_data use "https://api.development.devicecheck.apple.com"
public interface AppleDeviceApiClient {

    @PostMapping(value = "https://api.development.devicecheck.apple.com/v1/query_two_bits",
            headers = {"Accept=application/protobuf; version=1.0", "Authorization=Bearer ${ios.deviceapi.token}"})
    IosDeviceData getPerDeviceData(IosDeviceDataQueryRequest queryRequest);

    @PostMapping(value = "https://api.development.devicecheck.apple.com/v1/update_two_bits",
            headers = {"Accept=application/protobuf; version=1.0", "Authorization=Bearer ${ios.deviceapi.token}"})
    void updatePerDeviceData(IosDeviceDataUpdateRequest updateRequest);

    @PostMapping(value = "https://api.development.devicecheck.apple.com/v1/validate_device_token",
            headers = {"Accept=application/protobuf; version=1.0", "Authorization=Bearer ${ios.deviceapi.token}"})
    void validateDevice(IosDeviceValidationRequest iosDeviceValidationRequest);


}
