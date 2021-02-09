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
  private Integer exposureRiskMetadataRetentionDays;
  @Min(0)
  private Integer exposureWindowRetentionDays;
  @Min(0)
  private Integer keyMetadataWithClientRetentionDays;
  @Min(0)
  private Integer keyMetadataWithUserRetentionDays;
  @Min(0)
  private Integer testResultMetadataRetentionDays;
  @Min(0)
  private Integer saltRetentionDays;

  public Integer getTestResultMetadataRetentionDays() {
    return testResultMetadataRetentionDays;
  }

  public void setTestResultMetadataRetentionDays(Integer testResultMetadataRetentionDays) {
    this.testResultMetadataRetentionDays = testResultMetadataRetentionDays;
  }

  public Integer getSaltRetentionDays() {
    return saltRetentionDays;
  }

  public void setSaltRetentionDays(Integer saltRetentionDays) {
    this.saltRetentionDays = saltRetentionDays;
  }

  public Integer getKeyMetadataWithUserRetentionDays() {
    return keyMetadataWithUserRetentionDays;
  }

  public void setKeyMetadataWithUserRetentionDays(Integer keyMetadataWithUserRetentionDays) {
    this.keyMetadataWithUserRetentionDays = keyMetadataWithUserRetentionDays;
  }

  public Integer getExposureWindowRetentionDays() {
    return exposureWindowRetentionDays;
  }

  public void setExposureWindowRetentionDays(Integer exposureWindowRetentionDays) {
    this.exposureWindowRetentionDays = exposureWindowRetentionDays;
  }

  public Integer getKeyMetadataWithClientRetentionDays() {
    return keyMetadataWithClientRetentionDays;
  }

  public void setKeyMetadataWithClientRetentionDays(Integer keyMetadataWithClientRetentionDays) {
    this.keyMetadataWithClientRetentionDays = keyMetadataWithClientRetentionDays;
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

  public Integer getExposureRiskMetadataRetentionDays() {
    return exposureRiskMetadataRetentionDays;
  }

  public void setExposureRiskMetadataRetentionDays(Integer exposureRiskMetadataRetentionDays) {
    this.exposureRiskMetadataRetentionDays = exposureRiskMetadataRetentionDays;
  }
}
