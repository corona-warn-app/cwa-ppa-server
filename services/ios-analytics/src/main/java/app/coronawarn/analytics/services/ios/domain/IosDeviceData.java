package app.coronawarn.analytics.services.ios.domain;

import java.time.OffsetDateTime;

public class IosDeviceData {

    boolean bit0;
    boolean bit1;
    String last_update_time; // YYYY-MM


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

    public String getLast_update_time() {
        return last_update_time;
    }

    public void setLast_update_time(String last_update_time) {
        this.last_update_time = last_update_time;
    }
}
