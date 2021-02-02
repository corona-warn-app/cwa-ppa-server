package app.coronawarn.datadonation.services.retention.config;

import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "services.retention")
@Validated
public class RetentionConfiguration {

  @Min(0)
  private Integer apiTokenRetentionDays;
  @Min(0)
  private Integer deviceTokenRetentionDays;
  @Min(0)
  private Integer otpRetentionDays;
  @Min(0)
  private Integer analyticsDataRetentionDays;

  public Integer getApiTokenRetentionDays() {
    return apiTokenRetentionDays;
  }

  public void setApiTokenRetentionDays(Integer apiTokenRetentionDays) {
    this.apiTokenRetentionDays = apiTokenRetentionDays;
  }

  public Integer getDeviceTokenRetentionDays() {
    return deviceTokenRetentionDays;
  }

  public void setDeviceTokenRetentionDays(Integer deviceTokenRetentionDays) {
    this.deviceTokenRetentionDays = deviceTokenRetentionDays;
  }

  public Integer getOtpRetentionDays() {
    return otpRetentionDays;
  }

  public void setOtpRetentionDays(Integer otpRetentionDays) {
    this.otpRetentionDays = otpRetentionDays;
  }

  public Integer getAnalyticsDataRetentionDays() {
    return analyticsDataRetentionDays;
  }

  public void setAnalyticsDataRetentionDays(Integer analyticsDataRetentionDays) {
    this.analyticsDataRetentionDays = analyticsDataRetentionDays;
  }
}
