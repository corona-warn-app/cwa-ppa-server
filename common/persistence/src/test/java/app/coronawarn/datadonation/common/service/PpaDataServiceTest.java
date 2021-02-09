package app.coronawarn.datadonation.common.service;

import app.coronawarn.datadonation.common.persistence.domain.metrics.KeySubmissionMetadataWithClientMetadata;
import app.coronawarn.datadonation.common.persistence.repository.metrics.*;
import app.coronawarn.datadonation.common.persistence.service.PpaDataRequestIosConverter;
import app.coronawarn.datadonation.common.persistence.service.PpaDataService;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAKeySubmissionMetadata;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PpaDataRequestIos.PPADataRequestIOS;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
  private PpaDataRequestIosConverter converter;

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
  }

}
