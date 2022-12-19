package app.coronawarn.datadonation.services.ppac.config;

import java.util.HexFormat;
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

      public void setRequireAndroidIdSyntaxCheck(final Boolean requireAndroidIdSyntaxCheck) {
        this.requireAndroidIdSyntaxCheck = requireAndroidIdSyntaxCheck;
      }

      public void setRequireBasicIntegrity(final Boolean requireBasicIntegrity) {
        this.requireBasicIntegrity = requireBasicIntegrity;
      }

      public void setRequireCtsProfileMatch(final Boolean requireCtsProfileMatch) {
        this.requireCtsProfileMatch = requireCtsProfileMatch;
      }

      public void setRequireEvaluationTypeBasic(final Boolean requireEvaluationTypeBasic) {
        this.requireEvaluationTypeBasic = requireEvaluationTypeBasic;
      }

      public void setRequireEvaluationTypeHardwareBacked(final Boolean requireEvaluationTypeHardwareBacked) {
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
      @Min(1)
      private int minAndroidIdLength;

      @Max(Integer.MAX_VALUE)
      private int maxAndroidIdLength;

      public int getMaxAndroidIdLength() {
        return maxAndroidIdLength;
      }

      public int getMinAndroidIdLength() {
        return minAndroidIdLength;
      }

      public void setMaxAndroidIdLength(final int maxAndroidIdLength) {
        this.maxAndroidIdLength = maxAndroidIdLength;
      }

      public void setMinAndroidIdLength(final int minAndroidIdLength) {
        this.minAndroidIdLength = minAndroidIdLength;
      }
    }

    @NotEmpty
    private String[] allowedApkCertificateDigests;
    @NotEmpty
    private String[] allowedApkPackageNames;
    /**
     * A 16 random byte sequence as hex representation - unique per environment.
     */
    @Pattern(regexp = "[0-9a-fA-F]{32}")
    private String androidIdPepper;

    private Integer attestationValidity;

    @NotEmpty
    private String certificateHostname;

    private Dat dat;

    @NotNull
    private Boolean disableApkCertificateDigestsCheck;

    @NotNull
    private Boolean disableNonceCheck;

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

    public byte[] pepper() {
      return HexFormat.of().parseHex(getAndroidIdPepper());
    }

    public void setAllowedApkCertificateDigests(final String[] allowedApkCertificateDigests) {
      this.allowedApkCertificateDigests = allowedApkCertificateDigests;
    }

    public void setAllowedApkPackageNames(final String[] allowedApkPackageNames) {
      this.allowedApkPackageNames = allowedApkPackageNames;
    }

    public void setAndroidIdPepper(final String androidIdPepper) {
      this.androidIdPepper = androidIdPepper;
    }

    public void setAttestationValidity(final Integer attestationValidity) {
      this.attestationValidity = attestationValidity;
    }

    public void setCertificateHostname(final String certificateHostname) {
      this.certificateHostname = certificateHostname;
    }

    public void setDat(final Dat dat) {
      this.dat = dat;
    }

    public void setDisableApkCertificateDigestsCheck(final Boolean disableApkCertificateDigestsCheck) {
      this.disableApkCertificateDigestsCheck = disableApkCertificateDigestsCheck;
    }

    public void setDisableNonceCheck(final Boolean disableNonceCheck) {
      this.disableNonceCheck = disableNonceCheck;
    }

    public void setLog(final Log log) {
      this.log = log;
    }

    public void setOtp(final Otp otp) {
      this.otp = otp;
    }

    public void setSrs(final Srs srs) {
      this.srs = srs;
    }
  }

  public static final class Ios {

    private Integer apiTokenRateLimitSeconds;
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

    private int srsApiTokenRateLimitSeconds;

    public Integer getApiTokenRateLimitSeconds() {
      return apiTokenRateLimitSeconds;
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

    public int getSrsApiTokenRateLimitSeconds() {
      return srsApiTokenRateLimitSeconds;
    }

    public void setApiTokenRateLimitSeconds(final Integer apiTokenRateLimitSeconds) {
      this.apiTokenRateLimitSeconds = apiTokenRateLimitSeconds;
    }

    public void setDeviceApiUrl(final String deviceApiUrl) {
      this.deviceApiUrl = deviceApiUrl;
    }

    public void setMaxDeviceTokenLength(final Integer maxDeviceTokenLength) {
      this.maxDeviceTokenLength = maxDeviceTokenLength;
    }

    public void setMinDeviceTokenLength(final Integer minDeviceTokenLength) {
      this.minDeviceTokenLength = minDeviceTokenLength;
    }

    public void setMissingOrIncorrectlyFormattedDeviceTokenPayload(
        final String missingOrIncorrectlyFormattedDeviceTokenPayload) {
      this.missingOrIncorrectlyFormattedDeviceTokenPayload = missingOrIncorrectlyFormattedDeviceTokenPayload;
    }

    public void setPpacIosJwtKeyId(final String ppacIosJwtKeyId) {
      this.ppacIosJwtKeyId = ppacIosJwtKeyId;
    }

    public void setPpacIosJwtSigningKey(final String ppacIosJwtSigningKey) {
      this.ppacIosJwtSigningKey = ppacIosJwtSigningKey;
    }

    public void setPpacIosJwtTeamId(final String ppacIosJwtTeamId) {
      this.ppacIosJwtTeamId = ppacIosJwtTeamId;
    }

    public void setSrsApiTokenRateLimitSeconds(final int srsApiTokenRateLimitSeconds) {
      this.srsApiTokenRateLimitSeconds = srsApiTokenRateLimitSeconds;
    }
  }

  private Android android;

  @Min(1)
  @Max(100)
  private long fakeDelayMovingAverageSamples;

  /**
   * Exponential moving average of the last N real request durations (in ms), where N = fakeDelayMovingAverageSamples.
   */
  @Min(1)
  @Max(3000)
  private long initialFakeDelayMilliseconds;

  private Ios ios;

  private int maxExposureWindowsToRejectSubmission;

  private int maxExposureWindowsToStore;

  @Min(1)
  @Max(1000)
  private long monitoringBatchSize;

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

  public long getFakeDelayMovingAverageSamples() {
    return fakeDelayMovingAverageSamples;
  }

  public long getInitialFakeDelayMilliseconds() {
    return initialFakeDelayMilliseconds;
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

  public long getMonitoringBatchSize() {
    return monitoringBatchSize;
  }

  public int getOtpValidityInHours() {
    return otpValidityInHours;
  }

  public int getSrsOtpValidityInMinutes() {
    return srsOtpValidityInMinutes;
  }

  public int getSrsTimeBetweenSubmissionsInDays() {
    return srsTimeBetweenSubmissionsInDays;
  }

  public void setAndroid(final Android android) {
    this.android = android;
  }

  public void setFakeDelayMovingAverageSamples(final long fakeDelayMovingAverageSamples) {
    this.fakeDelayMovingAverageSamples = fakeDelayMovingAverageSamples;
  }

  public void setInitialFakeDelayMilliseconds(final long initialFakeDelayMilliseconds) {
    this.initialFakeDelayMilliseconds = initialFakeDelayMilliseconds;
  }

  public void setIos(final Ios ios) {
    this.ios = ios;
  }

  public void setMaxExposureWindowsToRejectSubmission(final int maxExposureWindowsToRejectSubmission) {
    this.maxExposureWindowsToRejectSubmission = maxExposureWindowsToRejectSubmission;
  }

  public void setMaxExposureWindowsToStore(final int maxExposureWindowsToStore) {
    this.maxExposureWindowsToStore = maxExposureWindowsToStore;
  }

  public void setMonitoringBatchSize(final long monitoringBatchSize) {
    this.monitoringBatchSize = monitoringBatchSize;
  }

  public void setOtpValidityInHours(final int otpValidityInHours) {
    this.otpValidityInHours = otpValidityInHours;
  }

  public void setSrsOtpValidityInMinutes(final int srsOtpValidityInMinutes) {
    this.srsOtpValidityInMinutes = srsOtpValidityInMinutes;
  }

  public void setSrsTimeBetweenSubmissionsInDays(final int srsTimeBetweenSubmissionsInDays) {
    this.srsTimeBetweenSubmissionsInDays = srsTimeBetweenSubmissionsInDays;
  }
}
