package app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Embedded.OnEmpty;

/**
 * The following properties are client metadata that are inlined per record to avoid that correlating entries from the
 * same submission (no ETag reference here).
 */
public class ClientMetadataDetails {

  @Valid
  @NotNull
  @Embedded(onEmpty = OnEmpty.USE_EMPTY)
  private CwaVersionMetadata cwaVersion;

  @NotNull
  private final String appConfigEtag;
  private final Integer iosVersionMajor;
  private final Integer iosVersionMinor;
  private final Integer iosVersionPatch;
  private final Long androidApiLevel;
  private final Long androidEnfVersion;

  /**
   * Constructs an immutable instance.
   */
  public ClientMetadataDetails(CwaVersionMetadata cwaVersion, String appConfigEtag, Integer iosVersionMajor,
      Integer iosVersionMinor, Integer iosVersionPatch, Long androidApiLevel,
      Long androidEnfVersion) {
    this.appConfigEtag = appConfigEtag;
    this.iosVersionMajor = iosVersionMajor;
    this.iosVersionMinor = iosVersionMinor;
    this.iosVersionPatch = iosVersionPatch;
    this.androidApiLevel = androidApiLevel;
    this.androidEnfVersion = androidEnfVersion;
    this.cwaVersion = cwaVersion;
  }

  public CwaVersionMetadata getCwaVersion() {
    return cwaVersion;
  }

  public String getAppConfigEtag() {
    return appConfigEtag;
  }

  public Integer getIosVersionMajor() {
    return iosVersionMajor;
  }

  public Integer getIosVersionMinor() {
    return iosVersionMinor;
  }

  public Integer getIosVersionPatch() {
    return iosVersionPatch;
  }

  public Long getAndroidApiLevel() {
    return androidApiLevel;
  }

  public Long getAndroidEnfVersion() {
    return androidEnfVersion;
  }

  @Override
  public int hashCode() {
    return Objects.hash(androidApiLevel, androidEnfVersion, appConfigEtag,
        iosVersionMajor, iosVersionMinor, iosVersionPatch);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    ClientMetadataDetails other = (ClientMetadataDetails) obj;
    if (androidApiLevel == null) {
      if (other.androidApiLevel != null) {
        return false;
      }
    } else if (!androidApiLevel.equals(other.androidApiLevel)) {
      return false;
    }
    if (androidEnfVersion == null) {
      if (other.androidEnfVersion != null) {
        return false;
      }
    } else if (!androidEnfVersion.equals(other.androidEnfVersion)) {
      return false;
    }
    if (appConfigEtag == null) {
      if (other.appConfigEtag != null) {
        return false;
      }
    } else if (!appConfigEtag.equals(other.appConfigEtag)) {
      return false;
    }
    if (cwaVersion == null) {
      if (other.cwaVersion != null) {
        return false;
      }
    } else if (!cwaVersion.equals(other.cwaVersion)) {
      return false;
    }
    if (iosVersionMajor == null) {
      if (other.iosVersionMajor != null) {
        return false;
      }
    } else if (!iosVersionMajor.equals(other.iosVersionMajor)) {
      return false;
    }
    if (iosVersionMinor == null) {
      if (other.iosVersionMinor != null) {
        return false;
      }
    } else if (!iosVersionMinor.equals(other.iosVersionMinor)) {
      return false;
    }
    if (iosVersionPatch == null) {
      if (other.iosVersionPatch != null) {
        return false;
      }
    } else if (!iosVersionPatch.equals(other.iosVersionPatch)) {
      return false;
    }
    return true;
  }
}
