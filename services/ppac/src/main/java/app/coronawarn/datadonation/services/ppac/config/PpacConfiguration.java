package app.coronawarn.datadonation.services.ppac.config;

import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ppac")
@Validated
public class PpacConfiguration {

  private Ios ios;
  private Android android;

  public Ios getIos() {
    return ios;
  }

  public void setIos(Ios ios) {
    this.ios = ios;
  }

  public Android getAndroid() {
    return android;
  }

  public void setAndroid(Android android) {
    this.android = android;
  }

  public static final class Ios {

    @NotEmpty
    private String ppacIosJwtKeyId;
    @NotEmpty
    private String ppacIosJwtTeamId;
    @NotEmpty
    private String deviceIdentificationUrl;

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
  }

  public static final class Android {
    
    @NotEmpty
    private String certificateHostname;
    private Integer attestationValidity;
    @NotEmpty
    private String[] allowedApkPackageNames;
    @NotEmpty
    private String[] allowedApkCertificateDigests;

    public String getCertificateHostname() {
      return certificateHostname;
    }

    public void setCertificateHostname(String certificateHostname) {
      this.certificateHostname = certificateHostname;
    }

    public Integer getAttestationValidity() {
      return attestationValidity;
    }

    public void setAttestationValidity(Integer attestationValidity) {
      this.attestationValidity = attestationValidity;
    }

    public String[] getAllowedApkPackageNames() {
      return allowedApkPackageNames;
    }

    public void setAllowedApkPackageNames(String[] allowedApkPackageNames) {
      this.allowedApkPackageNames = allowedApkPackageNames;
    }

    public String[] getAllowedApkCertificateDigests() {
      return allowedApkCertificateDigests;
    }

    public void setAllowedApkCertificateDigests(String[] allowedApkCertificateDigests) {
      this.allowedApkCertificateDigests = allowedApkCertificateDigests;
    }
  }
}
