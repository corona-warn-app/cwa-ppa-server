package app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
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
  private final CwaVersionMetadata cwaVersion;

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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientMetadataDetails that = (ClientMetadataDetails) o;
    return Objects.equals(iosVersionMajor, that.iosVersionMajor)
        && Objects.equals(iosVersionMinor, that.iosVersionMinor)
        && Objects.equals(iosVersionPatch, that.iosVersionPatch)
        && Objects.equals(androidApiLevel, that.androidApiLevel)
        && Objects.equals(androidEnfVersion, that.androidEnfVersion)
        && Objects.equals(appConfigEtag, that.appConfigEtag)
        && Objects.equals(cwaVersion, that.cwaVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cwaVersion, appConfigEtag, iosVersionMajor, iosVersionMinor,
        iosVersionPatch, androidApiLevel, androidEnfVersion);
  }
}
