package app.coronawarn.analytics.common.persistence.domain;

/**
 * A key generated for advertising over a window of time.
 */
public class AnalyticsData {

    String deviceToken; // TODO FR comes from header maybe
    String apiToken; // TODO FR comes from header maybe
    Object analyticsData;


    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Object getAnalyticsData() {
        return analyticsData;
    }

    public void setAnalyticsData(Object analyticsData) {
        this.analyticsData = analyticsData;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}