package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * The following properties are client metadata that are inlined per record to avoid that correlating
 * entries from the same submission (no ETag reference here).
 */
public class ClientMetadata {

  @NotNull
  private final Integer cwaVersionMajor;
  @NotNull
  private final Integer cwaVersionMinor;
  @NotNull
  private final Integer cwaVersionPatch;
  @NotNull
  private final String appConfigEtag;
  private final Integer iosVersionMajor;
  private final Integer iosVersionMinor;
  private final Integer iosVersionPatch;
  private final Integer androidApiLevel;
  private final Integer androidEnfVersion;

  /**
   * Constructs an immutable instance.
   */
  public ClientMetadata(Integer cwaVersionMajor, Integer cwaVersionMinor,
      Integer cwaVersionPatch, String appConfigEtag, Integer iosVersionMajor,
      Integer iosVersionMinor, Integer iosVersionPatch, Integer androidApiLevel,
      Integer androidEnfVersion) {
    this.cwaVersionMajor = cwaVersionMajor;
    this.cwaVersionMinor = cwaVersionMinor;
    this.cwaVersionPatch = cwaVersionPatch;
    this.appConfigEtag = appConfigEtag;
    this.iosVersionMajor = iosVersionMajor;
    this.iosVersionMinor = iosVersionMinor;
    this.iosVersionPatch = iosVersionPatch;
    this.androidApiLevel = androidApiLevel;
    this.androidEnfVersion = androidEnfVersion;
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

  public Integer getAndroidApiLevel() {
    return androidApiLevel;
  }

  public Integer getAndroidEnfVersion() {
    return androidEnfVersion;
  }

  @Override
  public int hashCode() {
    return Objects.hash(androidApiLevel, androidEnfVersion, appConfigEtag, cwaVersionMajor,
        cwaVersionMinor, cwaVersionPatch, iosVersionMajor, iosVersionMinor, iosVersionPatch);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    ClientMetadata other = (ClientMetadata) obj;
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
