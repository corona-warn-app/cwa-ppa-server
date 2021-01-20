package app.coronawarn.analytics.services.ios.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IosDeviceData {

    boolean bit0;
    boolean bit1;
    @JsonProperty("last_update_time")
    String lastUpdated; // YYYY-MM

    public IosDeviceData() {
        // empty constructor
    }

    public IosDeviceData(boolean bit0, boolean bit1, String lastUpdated) {
        this.bit0 = bit0;
        this.bit1 = bit1;
        this.lastUpdated = lastUpdated;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
