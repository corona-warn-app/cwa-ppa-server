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
  private Integer deviceTokenRetentionHours;
  @Min(0)
  private Integer otpRetentionDays;
  @Min(0)
  private Integer elsOtpRetentionDays;
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
  private Integer saltRetentionHours;
  @Min(0)
  private Integer clientMetadataRetentionDays;
  @Min(0)
  private Integer userMetadataRetentionDays;

  public Integer getTestResultMetadataRetentionDays() {
    return testResultMetadataRetentionDays;
  }

  public void setTestResultMetadataRetentionDays(Integer testResultMetadataRetentionDays) {
    this.testResultMetadataRetentionDays = testResultMetadataRetentionDays;
  }

  public Integer getSaltRetentionHours() {
    return saltRetentionHours;
  }

  public void setSaltRetentionHours(Integer saltRetentionHours) {
    this.saltRetentionHours = saltRetentionHours;
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

  public Integer getDeviceTokenRetentionHours() {
    return deviceTokenRetentionHours;
  }

  public void setDeviceTokenRetentionHours(Integer deviceTokenRetentionHours) {
    this.deviceTokenRetentionHours = deviceTokenRetentionHours;
  }

  public Integer getOtpRetentionDays() {
    return otpRetentionDays;
  }

  public void setOtpRetentionDays(Integer otpRetentionDays) {
    this.otpRetentionDays = otpRetentionDays;
  }

  public Integer getExposureRiskMetadataRetentionDays() {
    return exposureRiskMetadataRetentionDays;
  }

  public void setExposureRiskMetadataRetentionDays(Integer exposureRiskMetadataRetentionDays) {
    this.exposureRiskMetadataRetentionDays = exposureRiskMetadataRetentionDays;
  }

  public Integer getClientMetadataRetentionDays() {
    return clientMetadataRetentionDays;
  }

  public void setClientMetadataRetentionDays(Integer clientMetadataRetentionDays) {
    this.clientMetadataRetentionDays = clientMetadataRetentionDays;
  }

  public Integer getElsOtpRetentionDays() {
    return elsOtpRetentionDays;
  }

  public void setElsOtpRetentionDays(Integer elsOtpRetentionDays) {
    this.elsOtpRetentionDays = elsOtpRetentionDays;
  }

  public Integer getUserMetadataRetentionDays() {
    return userMetadataRetentionDays;
  }

  public void setUserMetadataRetentionDays(Integer userMetadataRetentionDays) {
    this.userMetadataRetentionDays = userMetadataRetentionDays;
  }
}
