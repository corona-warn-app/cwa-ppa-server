package app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable;

import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * The following properties are user metadata that are inlined per metrics record to avoid correlating entries from the
 * same submission.
 */
public class UserMetadataDetails {

  /**
   * A number representing the federal state (Bundesland) of the user.
   */
  @NotNull
  private final Integer federalState;
  /**
   * A number representing the administrative unit (Keis, Bezirk, etc.) of the user (KreisIdSurvNet).
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
  public UserMetadataDetails(Integer federalState, Integer administrativeUnit, Integer ageGroup) {
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

    UserMetadataDetails other = (UserMetadataDetails) obj;
    return Objects.equals(administrativeUnit, other.administrativeUnit) 
        && Objects.equals(ageGroup, other.ageGroup)
        && Objects.equals(federalState, other.federalState);
  }
}
