package app.coronawarn.datadonation.services.retention.config;

import javax.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "services.retention")
@Validated
public class RetentionConfiguration {

  @Min(0)
  private int apiTokenRetentionDays;
  @Min(0)
  private int clientMetadataRetentionDays;
  @Min(0)
  private int deviceTokenRetentionHours;
  @Min(0)
  private int elsOtpRetentionDays;
  @Min(0)
  private int exposureRiskMetadataRetentionDays;
  @Min(0)
  private int exposureWindowAtTestRegistrationRetentionDays;
  @Min(0)
  private int exposureWindowRetentionDays;
  @Min(0)
  private int exposureWindowTestResultRetentionDays;
  @Min(0)
  private int keyMetadataWithClientRetentionDays;
  @Min(0)
  private int keyMetadataWithUserRetentionDays;
  @Min(0)
  private int otpRetentionDays;
  @Min(0)
  private int saltRetentionHours;
  @Min(0)
  private int scanInstanceAtTestRegistrationRetentionDays;
  @Min(0)
  private int srsOtpRetentionDays;
  @Min(0)
  private int timeBetweenSubmissionsInDays;

  @Min(0)
  private int summarizedExposureWindowRetentionDays;

  @Min(0)
  private int testResultMetadataRetentionDays;

  @Min(0)
  private int userMetadataRetentionDays;

  public int getApiTokenRetentionDays() {
    return apiTokenRetentionDays;
  }

  public int getClientMetadataRetentionDays() {
    return clientMetadataRetentionDays;
  }

  public int getDeviceTokenRetentionHours() {
    return deviceTokenRetentionHours;
  }

  public int getElsOtpRetentionDays() {
    return elsOtpRetentionDays;
  }

  public int getExposureRiskMetadataRetentionDays() {
    return exposureRiskMetadataRetentionDays;
  }

  public int getExposureWindowAtTestRegistrationRetentionDays() {
    return exposureWindowAtTestRegistrationRetentionDays;
  }

  public int getExposureWindowRetentionDays() {
    return exposureWindowRetentionDays;
  }

  public int getExposureWindowTestResultRetentionDays() {
    return exposureWindowTestResultRetentionDays;
  }

  public int getKeyMetadataWithClientRetentionDays() {
    return keyMetadataWithClientRetentionDays;
  }

  public int getKeyMetadataWithUserRetentionDays() {
    return keyMetadataWithUserRetentionDays;
  }

  public int getOtpRetentionDays() {
    return otpRetentionDays;
  }

  public int getSaltRetentionHours() {
    return saltRetentionHours;
  }

  public int getScanInstanceAtTestRegistrationRetentionDays() {
    return scanInstanceAtTestRegistrationRetentionDays;
  }

  public int getSrsOtpRetentionDays() {
    return srsOtpRetentionDays;
  }

  public int getSummarizedExposureWindowRetentionDays() {
    return summarizedExposureWindowRetentionDays;
  }

  public int getTestResultMetadataRetentionDays() {
    return testResultMetadataRetentionDays;
  }

  public int getTimeBetweenSubmissionsInDays() {
    return timeBetweenSubmissionsInDays;
  }

  public int getUserMetadataRetentionDays() {
    return userMetadataRetentionDays;
  }

  public void setApiTokenRetentionDays(final int apiTokenRetentionDays) {
    this.apiTokenRetentionDays = apiTokenRetentionDays;
  }

  public void setClientMetadataRetentionDays(final int clientMetadataRetentionDays) {
    this.clientMetadataRetentionDays = clientMetadataRetentionDays;
  }

  public void setDeviceTokenRetentionHours(final int deviceTokenRetentionHours) {
    this.deviceTokenRetentionHours = deviceTokenRetentionHours;
  }

  public void setElsOtpRetentionDays(final int elsOtpRetentionDays) {
    this.elsOtpRetentionDays = elsOtpRetentionDays;
  }

  public void setExposureRiskMetadataRetentionDays(final int exposureRiskMetadataRetentionDays) {
    this.exposureRiskMetadataRetentionDays = exposureRiskMetadataRetentionDays;
  }

  public void setExposureWindowAtTestRegistrationRetentionDays(
      final int exposureWindowAtTestRegistrationRetentionDays) {
    this.exposureWindowAtTestRegistrationRetentionDays = exposureWindowAtTestRegistrationRetentionDays;
  }

  public void setExposureWindowRetentionDays(final int exposureWindowRetentionDays) {
    this.exposureWindowRetentionDays = exposureWindowRetentionDays;
  }

  public void setExposureWindowTestResultRetentionDays(final int exposureWindowTestResultRetentionDays) {
    this.exposureWindowTestResultRetentionDays = exposureWindowTestResultRetentionDays;
  }

  public void setKeyMetadataWithClientRetentionDays(final int keyMetadataWithClientRetentionDays) {
    this.keyMetadataWithClientRetentionDays = keyMetadataWithClientRetentionDays;
  }

  public void setKeyMetadataWithUserRetentionDays(final int keyMetadataWithUserRetentionDays) {
    this.keyMetadataWithUserRetentionDays = keyMetadataWithUserRetentionDays;
  }

  public void setOtpRetentionDays(final int otpRetentionDays) {
    this.otpRetentionDays = otpRetentionDays;
  }

  public void setSaltRetentionHours(final int saltRetentionHours) {
    this.saltRetentionHours = saltRetentionHours;
  }

  public void setScanInstanceAtTestRegistrationRetentionDays(final int scanInstanceAtTestRegistrationRetentionDays) {
    this.scanInstanceAtTestRegistrationRetentionDays = scanInstanceAtTestRegistrationRetentionDays;
  }

  public void setSrsOtpRetentionDays(final int srsOtpRetentionDays) {
    this.srsOtpRetentionDays = srsOtpRetentionDays;
  }

  public void setSummarizedExposureWindowRetentionDays(final int summarizedExposureWindowRetentionDays) {
    this.summarizedExposureWindowRetentionDays = summarizedExposureWindowRetentionDays;
  }

  public void setTestResultMetadataRetentionDays(final int testResultMetadataRetentionDays) {
    this.testResultMetadataRetentionDays = testResultMetadataRetentionDays;
  }

  public void setTimeBetweenSubmissionsInDays(final int timeBetweenSubmissionsInDays) {
    this.timeBetweenSubmissionsInDays = timeBetweenSubmissionsInDays;
  }

  public void setUserMetadataRetentionDays(final int userMetadataRetentionDays) {
    this.userMetadataRetentionDays = userMetadataRetentionDays;
  }
}
