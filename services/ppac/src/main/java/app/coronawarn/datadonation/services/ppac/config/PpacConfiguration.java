package app.coronawarn.datadonation.services.ppac.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ppac")
@Validated
public class PpacConfiguration {

  public static final class Android {

    public static final class Dat {

      @NotNull
      private Boolean requireBasicIntegrity;
      @NotNull
      private Boolean requireCtsProfileMatch;
      @NotNull
      private Boolean requireEvaluationTypeBasic;
      @NotNull
      private Boolean requireEvaluationTypeHardwareBacked;

      public Boolean getRequireBasicIntegrity() {
        return requireBasicIntegrity;
      }

      public void setRequireBasicIntegrity(Boolean requireBasicIntegrity) {
        this.requireBasicIntegrity = requireBasicIntegrity;
      }

      public Boolean getRequireCtsProfileMatch() {
        return requireCtsProfileMatch;
      }

      public void setRequireCtsProfileMatch(Boolean requireCtsProfileMatch) {
        this.requireCtsProfileMatch = requireCtsProfileMatch;
      }

      public Boolean getRequireEvaluationTypeBasic() {
        return requireEvaluationTypeBasic;
      }

      public void setRequireEvaluationTypeBasic(Boolean requireEvaluationTypeBasic) {
        this.requireEvaluationTypeBasic = requireEvaluationTypeBasic;
      }

      public Boolean getRequireEvaluationTypeHardwareBacked() {
        return requireEvaluationTypeHardwareBacked;
      }

      public void setRequireEvaluationTypeHardwareBacked(Boolean requireEvaluationTypeHardwareBacked) {
        this.requireEvaluationTypeHardwareBacked = requireEvaluationTypeHardwareBacked;
      }
    }

    public static final class Otp {

      @NotNull
      private Boolean requireBasicIntegrity;
      @NotNull
      private Boolean requireCtsProfileMatch;
      @NotNull
      private Boolean requireEvaluationTypeBasic;
      @NotNull
      private Boolean requireEvaluationTypeHardwareBacked;

      public Boolean getRequireBasicIntegrity() {
        return requireBasicIntegrity;
      }

      public void setRequireBasicIntegrity(Boolean requireBasicIntegrity) {
        this.requireBasicIntegrity = requireBasicIntegrity;
      }

      public Boolean getRequireCtsProfileMatch() {
        return requireCtsProfileMatch;
      }

      public void setRequireCtsProfileMatch(Boolean requireCtsProfileMatch) {
        this.requireCtsProfileMatch = requireCtsProfileMatch;
      }

      public Boolean getRequireEvaluationTypeBasic() {
        return requireEvaluationTypeBasic;
      }

      public void setRequireEvaluationTypeBasic(Boolean requireEvaluationTypeBasic) {
        this.requireEvaluationTypeBasic = requireEvaluationTypeBasic;
      }

      public Boolean getRequireEvaluationTypeHardwareBacked() {
        return requireEvaluationTypeHardwareBacked;
      }

      public void setRequireEvaluationTypeHardwareBacked(Boolean requireEvaluationTypeHardwareBacked) {
        this.requireEvaluationTypeHardwareBacked = requireEvaluationTypeHardwareBacked;
      }
    }

    private Log log;

    private Dat dat;
    private Otp otp;

    public Log getLog() {
      return log;
    }

    public void setLog(Log log) {
      this.log = log;
    }

    public static final class Log {

      @NotNull
      private Boolean requireBasicIntegrity;
      @NotNull
      private Boolean requireCtsProfileMatch;
      @NotNull
      private Boolean requireEvaluationTypeBasic;
      @NotNull
      private Boolean requireEvaluationTypeHardwareBacked;

      public Boolean getRequireBasicIntegrity() {
        return requireBasicIntegrity;
      }

      public void setRequireBasicIntegrity(Boolean requireBasicIntegrity) {
        this.requireBasicIntegrity = requireBasicIntegrity;
      }

      public Boolean getRequireCtsProfileMatch() {
        return requireCtsProfileMatch;
      }

      public void setRequireCtsProfileMatch(Boolean requireCtsProfileMatch) {
        this.requireCtsProfileMatch = requireCtsProfileMatch;
      }

      public Boolean getRequireEvaluationTypeBasic() {
        return requireEvaluationTypeBasic;
      }

      public void setRequireEvaluationTypeBasic(Boolean requireEvaluationTypeBasic) {
        this.requireEvaluationTypeBasic = requireEvaluationTypeBasic;
      }

      public Boolean getRequireEvaluationTypeHardwareBacked() {
        return requireEvaluationTypeHardwareBacked;
      }

      public void setRequireEvaluationTypeHardwareBacked(Boolean requireEvaluationTypeHardwareBacked) {
        this.requireEvaluationTypeHardwareBacked = requireEvaluationTypeHardwareBacked;
      }
    }

    @NotEmpty
    private String certificateHostname;
    private Integer attestationValidity;
    @NotEmpty
    private String[] allowedApkPackageNames;
    @NotEmpty
    private String[] allowedApkCertificateDigests;

    @NotNull
    private Boolean disableNonceCheck;
    @NotNull
    private Boolean disableApkCertificateDigestsCheck;

    public String[] getAllowedApkCertificateDigests() {
      return allowedApkCertificateDigests;
    }

    public String[] getAllowedApkPackageNames() {
      return allowedApkPackageNames;
    }

    public Integer getAttestationValidity() {
      return attestationValidity;
    }

    public String getCertificateHostname() {
      return certificateHostname;
    }

    public Boolean getDisableApkCertificateDigestsCheck() {
      return disableApkCertificateDigestsCheck;
    }

    public Boolean getDisableNonceCheck() {
      return disableNonceCheck;
    }

    public void setAllowedApkCertificateDigests(String[] allowedApkCertificateDigests) {
      this.allowedApkCertificateDigests = allowedApkCertificateDigests;
    }

    public void setAllowedApkPackageNames(String[] allowedApkPackageNames) {
      this.allowedApkPackageNames = allowedApkPackageNames;
    }

    public void setAttestationValidity(Integer attestationValidity) {
      this.attestationValidity = attestationValidity;
    }

    public void setCertificateHostname(String certificateHostname) {
      this.certificateHostname = certificateHostname;
    }

    public void setDisableApkCertificateDigestsCheck(Boolean disableApkCertificateDigestsCheck) {
      this.disableApkCertificateDigestsCheck = disableApkCertificateDigestsCheck;
    }

    public void setDisableNonceCheck(Boolean disableNonceCheck) {
      this.disableNonceCheck = disableNonceCheck;
    }

    public Dat getDat() {
      return dat;
    }

    public void setDat(Dat dat) {
      this.dat = dat;
    }

    public Otp getOtp() {
      return otp;
    }

    public void setOtp(Otp otp) {
      this.otp = otp;
    }
  }

  public static final class Ios {

    @NotEmpty
    private String ppacIosJwtKeyId;
    @NotEmpty
    private String ppacIosJwtTeamId;
    // TODO: Check how to exclude from tests
    // @NotEmpty
    private String deviceApiUrl;
    private String ppacIosJwtSigningKey;
    private Integer minDeviceTokenLength;
    private Integer maxDeviceTokenLength;
    private String missingOrIncorrectlyFormattedDeviceTokenPayload;

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

  private int otpValidityInHours;

  private int maxExposureWindowsToStore;
  private int maxExposureWindowsToRejectSubmission;

  private Ios ios;

  private Android android;

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
}
