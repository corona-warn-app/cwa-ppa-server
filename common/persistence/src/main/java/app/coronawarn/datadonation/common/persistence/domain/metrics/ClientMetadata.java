package app.coronawarn.datadonation.common.persistence.domain.metrics;

import java.util.Objects;

/**
 * The following properties are client metadata that are inlined per record to avoid that correlating
 * entries from the same submission (no ETag reference here).
 */
public class ClientMetadata {

  private final Integer cwa_version_major;
  private final Integer cwa_version_minor;
  private final Integer cwa_version_patch;
  private final String appConfigEtag;
  private final Integer iosVersionMajor;
  private final Integer iosVersionMinor;
  private final Integer iosVersionPatch;
  private final Integer androidApiLevel;
  private final Integer androidEnfVersion;

  /**
   * Constructs an immutable instance.
   */
  public ClientMetadata(Integer cwa_version_major, Integer cwa_version_minor,
      Integer cwa_version_patch, String appConfigEtag, Integer iosVersionMajor,
      Integer iosVersionMinor, Integer iosVersionPatch, Integer androidApiLevel,
      Integer androidEnfVersion) {
    this.cwa_version_major = cwa_version_major;
    this.cwa_version_minor = cwa_version_minor;
    this.cwa_version_patch = cwa_version_patch;
    this.appConfigEtag = appConfigEtag;
    this.iosVersionMajor = iosVersionMajor;
    this.iosVersionMinor = iosVersionMinor;
    this.iosVersionPatch = iosVersionPatch;
    this.androidApiLevel = androidApiLevel;
    this.androidEnfVersion = androidEnfVersion;
  }

  public Integer getCwa_version_major() {
    return cwa_version_major;
  }

  public Integer getCwa_version_minor() {
    return cwa_version_minor;
  }

  public Integer getCwa_version_patch() {
    return cwa_version_patch;
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
    return Objects.hash(androidApiLevel, androidEnfVersion, appConfigEtag, cwa_version_major,
        cwa_version_minor, cwa_version_patch, iosVersionMajor, iosVersionMinor, iosVersionPatch);
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
    if (cwa_version_major == null) {
      if (other.cwa_version_major != null) {
        return false;
      }
    } else if (!cwa_version_major.equals(other.cwa_version_major)) {
      return false;
    }
    if (cwa_version_minor == null) {
      if (other.cwa_version_minor != null) {
        return false;
      }
    } else if (!cwa_version_minor.equals(other.cwa_version_minor)) {
      return false;
    }
    if (cwa_version_patch == null) {
      if (other.cwa_version_patch != null) {
        return false;
      }
    } else if (!cwa_version_patch.equals(other.cwa_version_patch)) {
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
