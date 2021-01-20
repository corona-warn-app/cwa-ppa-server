package app.coronawarn.analytics.services.ios.domain;

public class IosDeviceDataQueryRequest {

  String device_token;
  String transaction_id;
  Long timestamp;

  public IosDeviceDataQueryRequest(String device_token, String transaction_id, Long timestamp) {
    this.device_token = device_token;
    this.transaction_id = transaction_id;
    this.timestamp = timestamp;
  }
}
