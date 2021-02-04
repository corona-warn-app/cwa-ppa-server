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
  private Integer otpRetentionHours;
  @Min(0)
  private Integer intDataRetentionDays;
  @Min(0)
  private Integer floatDataRetentionDays;
  @Min(0)
  private Integer textDataRetentionDays;
  @Min(0)
  private Integer saltRetentionDays;

  public Integer getSaltRetentionDays() {
    return saltRetentionDays;
  }

  public void setSaltRetentionDays(Integer saltRetentionDays) {
    this.saltRetentionDays = saltRetentionDays;
  }

  public Integer getFloatDataRetentionDays() {
    return floatDataRetentionDays;
  }

  public void setFloatDataRetentionDays(Integer floatDataRetentionDays) {
    this.floatDataRetentionDays = floatDataRetentionDays;
  }

  public Integer getTextDataRetentionDays() {
    return textDataRetentionDays;
  }

  public void setTextDataRetentionDays(Integer textDataRetentionDays) {
    this.textDataRetentionDays = textDataRetentionDays;
  }

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

  public Integer getOtpRetentionHours() {
    return otpRetentionHours;
  }

  public void setOtpRetentionHours(Integer otpRetentionHours) {
    this.otpRetentionHours = otpRetentionHours;
  }

  public Integer getIntDataRetentionDays() {
    return intDataRetentionDays;
  }

  public void setIntDataRetentionDays(Integer intDataRetentionDays) {
    this.intDataRetentionDays = intDataRetentionDays;
  }
}
