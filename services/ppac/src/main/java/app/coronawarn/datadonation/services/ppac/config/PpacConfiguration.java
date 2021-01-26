package app.coronawarn.datadonation.services.ppac.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ppac")
public class PpacConfiguration {

  private String ppacIosJwtKeyId;
  private String ppacIosJwtTeamId;
  private String deviceIdentificationUrl;
  private String ppacSigningKey;
  private Integer minDeviceTokenLength;
  private Integer maxDeviceTokenLength;

  public String getPpacIosJwtKeyId() {
    return ppacIosJwtKeyId;
  }

  public void setPpacIosJwtKeyId(String ppacIosJwtKeyId) {
    this.ppacIosJwtKeyId = ppacIosJwtKeyId;
  }

  public String getPpacIosJwtTeamId() {
    return ppacIosJwtTeamId;
  }

  public void setPpacIosJwtTeamId(String ppacIosJwtTeamId) {
    this.ppacIosJwtTeamId = ppacIosJwtTeamId;
  }

  public String getDeviceIdentificationUrl() {
    return deviceIdentificationUrl;
  }

  public void setDeviceIdentificationUrl(String deviceIdentificationUrl) {
    this.deviceIdentificationUrl = deviceIdentificationUrl;
  }

  public String getPpacSigningKey() {
    return ppacSigningKey;
  }

  public void setPpacSigningKey(String ppacSigningKey) {
    this.ppacSigningKey = ppacSigningKey;
  }

  public Integer getMinDeviceTokenLength() {
    return minDeviceTokenLength;
  }

  public void setMinDeviceTokenLength(Integer minDeviceTokenLength) {
    this.minDeviceTokenLength = minDeviceTokenLength;
  }

  public Integer getMaxDeviceTokenLength() {
    return maxDeviceTokenLength;
  }

  public void setMaxDeviceTokenLength(Integer maxDeviceTokenLength) {
    this.maxDeviceTokenLength = maxDeviceTokenLength;
  }
}
