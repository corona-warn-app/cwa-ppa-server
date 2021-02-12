package app.coronawarn.datadonation.services.edus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "services.edus")
@Validated
public class EdusConfig {

  public static class ConfigurationParameters {

    private Boolean requireBasicIntegrity;
    private Boolean requireCtsProfileMatch;
    private Boolean requireEvaluationTypeBasic;
    private Boolean requireEvaluationTypeHardwareBacked;
    private Integer otpValidityInHours;

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

    public Integer getOtpValidityInHours() {
      return otpValidityInHours;
    }

    public void setOtpValidityInHours(Integer otpValidityInHours) {
      this.otpValidityInHours = otpValidityInHours;
    }
  }

}
