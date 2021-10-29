package app.coronawarn.datadonation.services.ppac.android.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import app.coronawarn.datadonation.common.persistence.domain.metrics.TechnicalMetadata;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.ClientMetadataDetails;
import app.coronawarn.datadonation.common.persistence.domain.metrics.embeddable.CwaVersionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.ExposureRiskMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAClientMetadataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPARiskLevel;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPASemanticVersion;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAUserMetadata;
import app.coronawarn.datadonation.services.ppac.commons.PpaDataRequestConverter;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class PpaDataRequestAndroidConverterTest
    extends PpaDataRequestConverter<PPADataRequestAndroid, PPAClientMetadataAndroid> {

  @Test
  public void convertToExposureMetricsTestRiskLevelUnknownValue() {
    ExposureRiskMetadata exposureRiskMetadata = ExposureRiskMetadata.newBuilder()
        .setPtRiskLevelValue(PPARiskLevel.RISK_LEVEL_UNKNOWN_VALUE).build();

    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata dbExposureRiskMetadata =
        convertToExposureMetrics(
            Collections.singletonList(exposureRiskMetadata),
            PPAUserMetadata.getDefaultInstance(),
            TechnicalMetadata.newEmptyInstance(),
            PPAClientMetadataAndroid.getDefaultInstance());

    assertNull(dbExposureRiskMetadata.getPtRiskLevelChanged());
    assertNull(dbExposureRiskMetadata.getPtMostRecentDateAtRiskLevel());
    assertNull(dbExposureRiskMetadata.getPtMostRecentDateChanged());
  }

  @Test
  public void convertToExposureMetricsTest() {
    ExposureRiskMetadata exposureRiskMetadata = ExposureRiskMetadata.newBuilder()
        .setPtRiskLevelValue(PPARiskLevel.RISK_LEVEL_HIGH_VALUE)
        .setPtRiskLevelChangedComparedToPreviousSubmission(true)
        .setPtMostRecentDateAtRiskLevel(0)
        .setPtDateChangedComparedToPreviousSubmission(false)
        .build();

    app.coronawarn.datadonation.common.persistence.domain.metrics.ExposureRiskMetadata dbExposureRiskMetadata =
        convertToExposureMetrics(
            Collections.singletonList(exposureRiskMetadata),
            PPAUserMetadata.getDefaultInstance(),
            TechnicalMetadata.newEmptyInstance(),
            PPAClientMetadataAndroid.getDefaultInstance());

    assertTrue(dbExposureRiskMetadata.getPtRiskLevelChanged());
    assertEquals(LocalDate.of(1970, 1, 1), dbExposureRiskMetadata.getPtMostRecentDateAtRiskLevel());
    assertFalse(dbExposureRiskMetadata.getPtMostRecentDateChanged());
  }

  @Override
  protected ClientMetadataDetails convertToClientMetadataDetails(PPAClientMetadataAndroid clientMetadata) {
    PPASemanticVersion cwaVersion = clientMetadata.getCwaVersion();
    CwaVersionMetadata cwaVersionMetadata = new CwaVersionMetadata(cwaVersion.getMajor(), cwaVersion.getMinor(),
        cwaVersion.getPatch());
    return new ClientMetadataDetails(cwaVersionMetadata, clientMetadata.getAppConfigETag(), null, null, null,
        clientMetadata.getAndroidApiLevel(), clientMetadata.getEnfVersion());
  }

  @Override
  protected CwaVersionMetadata convertToCwaVersionMetadata(PPAClientMetadataAndroid clientMetadata) {
    return null;
  }
}
