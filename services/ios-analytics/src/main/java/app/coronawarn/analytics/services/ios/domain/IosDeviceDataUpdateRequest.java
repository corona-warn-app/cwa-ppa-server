package app.coronawarn.analytics.services.ios.domain;

public class IosDeviceDataUpdateRequest {

    String device_token;
    String transaction_id;
    Long timestamp;
    boolean bit0;
    boolean bit1;

    public IosDeviceDataUpdateRequest() {
    }

    public IosDeviceDataUpdateRequest(String device_token, String transaction_id, Long timestamp, boolean bit0, boolean bit1) {
        this.device_token = device_token;
        this.transaction_id = transaction_id;
        this.timestamp = timestamp;
        this.bit0 = bit0;
        this.bit1 = bit1;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isBit0() {
        return bit0;
    }

    public void setBit0(boolean bit0) {
        this.bit0 = bit0;
    }

    public boolean isBit1() {
        return bit1;
    }

    public void setBit1(boolean bit1) {
        this.bit1 = bit1;
    }
}
