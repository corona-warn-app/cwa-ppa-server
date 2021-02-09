package app.coronawarn.datadonation.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureRiskMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.ExposureWindowRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithClientMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.KeySubmissionMetadataWithUserMetadataRepository;
import app.coronawarn.datadonation.common.persistence.repository.metrics.TestResultMetadataRepository;
import app.coronawarn.datadonation.common.persistence.service.PpaDataRequestAndroidConverter;
import app.coronawarn.datadonation.common.persistence.service.PpaDataRequestIosConverter;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestAndroid.PPADataRequestAndroid;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class PpaDataServiceTest {

  @InjectMocks
  private PpaDataService underTest;

  @Mock
  private ExposureRiskMetadataRepository exposureRiskMetadataRepository;

  @Mock
  private ExposureWindowRepository exposureWindowRepository;

  @Mock
  private TestResultMetadataRepository testResultMetadataRepository;

  @Mock
  private KeySubmissionMetadataWithClientMetadataRepository keySubmissionMetadataWithClientMetadataRepository;

  @Mock
  private KeySubmissionMetadataWithUserMetadataRepository keySubmissionMetadataWithUserMetadataRepository;

  @Spy
  private PpaDataRequestIosConverter iosConverter;

  @Spy
  private PpaDataRequestAndroidConverter androidConverter;

  @Test
  public void testStoreForIos() {
    PPADataRequestIOS ppaDataRequestIOS = PPADataRequestIOS.newBuilder()
        .setPayload(PPADataIOS.newBuilder()
            .addKeySubmissionMetadataSet(PPAKeySubmissionMetadata.newBuilder()
                .setSubmittedAfterSymptomFlow(true).build()).build()).build();
    ArgumentCaptor<KeySubmissionMetadataWithClientMetadata> captor = ArgumentCaptor
        .forClass(KeySubmissionMetadataWithClientMetadata.class);
    underTest.storeForIos(ppaDataRequestIOS);
    verify(keySubmissionMetadataWithClientMetadataRepository, times(1)).save(captor.capture());
    assertThat(captor.getValue().getClientMetadata()).isNotNull();
    assertThat(captor.getValue().getClientMetadata().getAndroidApiLevel()).isNull();
    assertThat(captor.getValue().getClientMetadata().getAndroidEnfVersion()).isNull();
    assertThat(captor.getValue().getClientMetadata().getIosVersionMajor()).isNotNull();
    assertThat(captor.getValue().getClientMetadata().getIosVersionMinor()).isNotNull();
    assertThat(captor.getValue().getClientMetadata().getIosVersionPatch()).isNotNull();
  }

  @Test
  public void testStoreForAndroid() {
    PPADataRequestAndroid ppaDataRequestIOS = PPADataRequestAndroid.newBuilder()
        .setPayload(PPADataAndroid.newBuilder()
            .addKeySubmissionMetadataSet(PPAKeySubmissionMetadata.newBuilder()
                .setSubmittedAfterSymptomFlow(true).build()).build()).build();
    ArgumentCaptor<KeySubmissionMetadataWithClientMetadata> captor = ArgumentCaptor
        .forClass(KeySubmissionMetadataWithClientMetadata.class);
    underTest.storeForAndroid(ppaDataRequestIOS);
    verify(keySubmissionMetadataWithClientMetadataRepository, times(1)).save(captor.capture());
    assertThat(captor.getValue().getClientMetadata()).isNotNull();
    assertThat(captor.getValue().getClientMetadata().getAndroidApiLevel()).isNotNull();
    assertThat(captor.getValue().getClientMetadata().getAndroidEnfVersion()).isNotNull();
    assertThat(captor.getValue().getClientMetadata().getIosVersionMajor()).isNull();
    assertThat(captor.getValue().getClientMetadata().getIosVersionMinor()).isNull();
    assertThat(captor.getValue().getClientMetadata().getIosVersionPatch()).isNull();
  }

}
