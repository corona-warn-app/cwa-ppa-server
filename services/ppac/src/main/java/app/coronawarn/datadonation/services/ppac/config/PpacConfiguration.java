package app.coronawarn.datadonation.services.ppac.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ppac")
@Validated
public class PpacConfiguration {

  public static final class Android {

    private static class CommonAndroidProperties {

      @NotNull
      private Boolean requireAndroidIdSyntaxCheck;
      @NotNull
      private Boolean requireBasicIntegrity;
      @NotNull
      private Boolean requireCtsProfileMatch;
      @NotNull
      private Boolean requireEvaluationTypeBasic;
      @NotNull
      private Boolean requireEvaluationTypeHardwareBacked;

      public Boolean getRequireAndroidIdSyntaxCheck() {
        return requireAndroidIdSyntaxCheck;
      }

      public Boolean getRequireBasicIntegrity() {
        return requireBasicIntegrity;
      }

      public Boolean getRequireCtsProfileMatch() {
        return requireCtsProfileMatch;
      }

      public Boolean getRequireEvaluationTypeBasic() {
        return requireEvaluationTypeBasic;
      }

      public Boolean getRequireEvaluationTypeHardwareBacked() {
        return requireEvaluationTypeHardwareBacked;
      }

      public void setRequireAndroidIdSyntaxCheck(Boolean requireAndroidIdSyntaxCheck) {
        this.requireAndroidIdSyntaxCheck = requireAndroidIdSyntaxCheck;
      }

      public void setRequireBasicIntegrity(Boolean requireBasicIntegrity) {
        this.requireBasicIntegrity = requireBasicIntegrity;
      }

      public void setRequireCtsProfileMatch(Boolean requireCtsProfileMatch) {
        this.requireCtsProfileMatch = requireCtsProfileMatch;
      }

      public void setRequireEvaluationTypeBasic(Boolean requireEvaluationTypeBasic) {
        this.requireEvaluationTypeBasic = requireEvaluationTypeBasic;
      }

      public void setRequireEvaluationTypeHardwareBacked(Boolean requireEvaluationTypeHardwareBacked) {
        this.requireEvaluationTypeHardwareBacked = requireEvaluationTypeHardwareBacked;
      }
    }

    public static final class Dat extends CommonAndroidProperties {
    }

    public static final class Log extends CommonAndroidProperties {
    }

    public static final class Otp extends CommonAndroidProperties {
    }

    public static final class Srs extends CommonAndroidProperties {
    }

    @NotEmpty
    private String[] allowedApkCertificateDigests;
    @NotEmpty
    private String[] allowedApkPackageNames;
    private Integer attestationValidity;

    @NotEmpty
    private String certificateHostname;

    private Dat dat;

    @NotNull
    private Boolean disableApkCertificateDigestsCheck;

    @NotNull
    private Boolean disableNonceCheck;

    /**
     * A 16 random byte sequence as hex representation - unique per environment.
     */
    @Pattern(regexp = "[0-9a-fA-F]{32}")
    private String androidIdPepper;

    private Log log;
    private Otp otp;
    private Srs srs;

    public String[] getAllowedApkCertificateDigests() {
      return allowedApkCertificateDigests;
    }

    public String[] getAllowedApkPackageNames() {
      return allowedApkPackageNames;
    }

    public String getAndroidIdPepper() {
      return androidIdPepper;
    }

    public Integer getAttestationValidity() {
      return attestationValidity;
    }

    public String getCertificateHostname() {
      return certificateHostname;
    }

    public Dat getDat() {
      return dat;
    }

    public Boolean getDisableApkCertificateDigestsCheck() {
      return disableApkCertificateDigestsCheck;
    }

    public Boolean getDisableNonceCheck() {
      return disableNonceCheck;
    }

    public Log getLog() {
      return log;
    }

    public Otp getOtp() {
      return otp;
    }

    public Srs getSrs() {
      return srs;
    }

    public void setAllowedApkCertificateDigests(String[] allowedApkCertificateDigests) {
      this.allowedApkCertificateDigests = allowedApkCertificateDigests;
    }

    public void setAllowedApkPackageNames(String[] allowedApkPackageNames) {
      this.allowedApkPackageNames = allowedApkPackageNames;
    }

    public void setAndroidIdPepper(final String androidIdPepper) {
      this.androidIdPepper = androidIdPepper;
    }

    public void setAttestationValidity(Integer attestationValidity) {
      this.attestationValidity = attestationValidity;
    }

    public void setCertificateHostname(String certificateHostname) {
      this.certificateHostname = certificateHostname;
    }

    public void setDat(Dat dat) {
      this.dat = dat;
    }

    public void setDisableApkCertificateDigestsCheck(Boolean disableApkCertificateDigestsCheck) {
      this.disableApkCertificateDigestsCheck = disableApkCertificateDigestsCheck;
    }

    public void setDisableNonceCheck(Boolean disableNonceCheck) {
      this.disableNonceCheck = disableNonceCheck;
    }

    public void setLog(Log log) {
      this.log = log;
    }

    public void setOtp(Otp otp) {
      this.otp = otp;
    }

    public void setSrs(Srs srs) {
      this.srs = srs;
    }
  }

  public static final class Ios {

    private Integer apiTokenRateLimitSeconds;
    private int srsApiTokenRateLimitSeconds;
    // @NotEmpty
    private String deviceApiUrl;
    private Integer maxDeviceTokenLength;
    private Integer minDeviceTokenLength;
    private String missingOrIncorrectlyFormattedDeviceTokenPayload;
    @NotEmpty
    private String ppacIosJwtKeyId;
    private String ppacIosJwtSigningKey;

    @NotEmpty
    private String ppacIosJwtTeamId;

    public Integer getApiTokenRateLimitSeconds() {
      return apiTokenRateLimitSeconds;
    }

    public int getSrsApiTokenRateLimitSeconds() {
      return srsApiTokenRateLimitSeconds;
    }

    public String getDeviceApiUrl() {
      return deviceApiUrl;
    }

    public Integer getMaxDeviceTokenLength() {
      return maxDeviceTokenLength;
    }

    public Integer getMinDeviceTokenLength() {
      return minDeviceTokenLength;
    }

    public String getMissingOrIncorrectlyFormattedDeviceTokenPayload() {
      return missingOrIncorrectlyFormattedDeviceTokenPayload;
    }

    public String getPpacIosJwtKeyId() {
      return ppacIosJwtKeyId;
    }

    public String getPpacIosJwtSigningKey() {
      return ppacIosJwtSigningKey;
    }

    public String getPpacIosJwtTeamId() {
      return ppacIosJwtTeamId;
    }

    public void setApiTokenRateLimitSeconds(Integer apiTokenRateLimitSeconds) {
      this.apiTokenRateLimitSeconds = apiTokenRateLimitSeconds;
    }

    public void setSrsApiTokenRateLimitSeconds(int srsApiTokenRateLimitSeconds) {
      this.srsApiTokenRateLimitSeconds = srsApiTokenRateLimitSeconds;
    }

    public void setDeviceApiUrl(String deviceApiUrl) {
      this.deviceApiUrl = deviceApiUrl;
    }

    public void setMaxDeviceTokenLength(Integer maxDeviceTokenLength) {
      this.maxDeviceTokenLength = maxDeviceTokenLength;
    }

    public void setMinDeviceTokenLength(Integer minDeviceTokenLength) {
      this.minDeviceTokenLength = minDeviceTokenLength;
    }

    public void setMissingOrIncorrectlyFormattedDeviceTokenPayload(
        String missingOrIncorrectlyFormattedDeviceTokenPayload) {
      this.missingOrIncorrectlyFormattedDeviceTokenPayload = missingOrIncorrectlyFormattedDeviceTokenPayload;
    }

    public void setPpacIosJwtKeyId(String ppacIosJwtKeyId) {
      this.ppacIosJwtKeyId = ppacIosJwtKeyId;
    }

    public void setPpacIosJwtSigningKey(String ppacIosJwtSigningKey) {
      this.ppacIosJwtSigningKey = ppacIosJwtSigningKey;
    }

    public void setPpacIosJwtTeamId(String ppacIosJwtTeamId) {
      this.ppacIosJwtTeamId = ppacIosJwtTeamId;
    }
  }

  private Android android;

  private Ios ios;
  private int maxExposureWindowsToRejectSubmission;

  private int maxExposureWindowsToStore;

  private int otpValidityInHours;

  @Min(0)
  @Max(1440)
  private int srsOtpValidityInMinutes;

  @Min(0)
  @Max(365)
  private int srsTimeBetweenSubmissionsInDays;

  public Android getAndroid() {
    return android;
  }

  public Ios getIos() {
    return ios;
  }

  public int getMaxExposureWindowsToRejectSubmission() {
    return maxExposureWindowsToRejectSubmission;
  }

  public int getMaxExposureWindowsToStore() {
    return maxExposureWindowsToStore;
  }

  public int getOtpValidityInHours() {
    return otpValidityInHours;
  }

  public void setAndroid(Android android) {
    this.android = android;
  }

  public void setIos(Ios ios) {
    this.ios = ios;
  }

  public void setMaxExposureWindowsToRejectSubmission(int maxExposureWindowsToRejectSubmission) {
    this.maxExposureWindowsToRejectSubmission = maxExposureWindowsToRejectSubmission;
  }

  public void setMaxExposureWindowsToStore(int maxExposureWindowsToStore) {
    this.maxExposureWindowsToStore = maxExposureWindowsToStore;
  }

  public void setOtpValidityInHours(int otpValidityInHours) {
    this.otpValidityInHours = otpValidityInHours;
  }

  public int getSrsOtpValidityInMinutes() {
    return srsOtpValidityInMinutes;
  }

  public int getSrsTimeBetweenSubmissionsInDays() {
    return srsTimeBetweenSubmissionsInDays;
  }

  public void setSrsOtpValidityInMinutes(int srsOtpValidityInMinutes) {
    this.srsOtpValidityInMinutes = srsOtpValidityInMinutes;
  }

  public void setSrsTimeBetweenSubmissionsInDays(int srsTimeBetweenSubmissionsInDays) {
    this.srsTimeBetweenSubmissionsInDays = srsTimeBetweenSubmissionsInDays;
  }
}
