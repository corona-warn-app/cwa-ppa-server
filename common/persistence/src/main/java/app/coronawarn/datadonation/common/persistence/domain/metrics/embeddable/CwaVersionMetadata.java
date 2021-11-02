package app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable;

import java.util.Objects;
import javax.validation.constraints.NotNull;

public class CwaVersionMetadata {

  @NotNull
  private final Integer cwaVersionMajor;
  @NotNull
  private final Integer cwaVersionMinor;
  @NotNull
  private final Integer cwaVersionPatch;

  /**
   * Constructs the cwa version wrapper.
   *
   * @param cwaVersionMajor major version of cwa
   * @param cwaVersionMinor minor version of cwa
   * @param cwaVersionPatch patch version of cwa
   */

  public CwaVersionMetadata(Integer cwaVersionMajor, Integer cwaVersionMinor, Integer cwaVersionPatch) {
    this.cwaVersionMajor = cwaVersionMajor;
    this.cwaVersionMinor = cwaVersionMinor;
    this.cwaVersionPatch = cwaVersionPatch;
  }

  public Integer getCwaVersionMajor() {
    return cwaVersionMajor;
  }

  public Integer getCwaVersionMinor() {
    return cwaVersionMinor;
  }

  public Integer getCwaVersionPatch() {
    return cwaVersionPatch;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cwaVersionMajor, cwaVersionMinor, cwaVersionPatch);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    CwaVersionMetadata other = (CwaVersionMetadata) obj;
    if (cwaVersionMajor == null) {
      if (other.cwaVersionMajor != null) {
        return false;
      }
    } else if (!cwaVersionMajor.equals(other.cwaVersionMajor)) {
      return false;
    }
    if (cwaVersionMinor == null) {
      if (other.cwaVersionMinor != null) {
        return false;
      }
    } else if (!cwaVersionMinor.equals(other.cwaVersionMinor)) {
      return false;
    }
    if (cwaVersionPatch == null) {
      if (other.cwaVersionPatch != null) {
        return false;
      }
    } else if (!cwaVersionPatch.equals(other.cwaVersionPatch)) {
      return false;
    }
    return true;
  }
}
