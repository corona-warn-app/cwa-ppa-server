package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * The following properties are user metadata that are inlined per metrics record to avoid
 * correlating entries from the same submission.
 */
public class UserMetadata {

  /**
   * A number representing the federal state (Bundesland) of the user.
   */
  @NotNull
  private final Integer federalState;
  /**
   * A number representing the administrative unit (Keis, Bezirk, etc.) of the user
   * (KreisIdSurvNet).
   */
  @NotNull
  private final Integer administrativeUnit;
  /**
   * A number representing the age group of the user.
   */
  @NotNull
  private final Integer ageGroup;


  /**
   * Construct an immutable instance.
   */
  public UserMetadata(Integer federalState, Integer administrativeUnit, Integer ageGroup) {
    this.federalState = federalState;
    this.administrativeUnit = administrativeUnit;
    this.ageGroup = ageGroup;
  }

  public Integer getFederalState() {
    return federalState;
  }

  public Integer getAdministrativeUnit() {
    return administrativeUnit;
  }

  public Integer getAgeGroup() {
    return ageGroup;
  }

  @Override
  public int hashCode() {
    return Objects.hash(administrativeUnit, ageGroup, federalState);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    UserMetadata other = (UserMetadata) obj;
    if (administrativeUnit == null) {
      if (other.administrativeUnit != null) {
        return false;
      }
    } else if (!administrativeUnit.equals(other.administrativeUnit)) {
      return false;
    }
    if (ageGroup == null) {
      if (other.ageGroup != null) {
        return false;
      }
    } else if (!ageGroup.equals(other.ageGroup)) {
      return false;
    }
    if (federalState == null) {
      if (other.federalState != null) {
        return false;
      }
    } else if (!federalState.equals(other.federalState)) {
      return false;
    }
    return true;
  }
}
