package app.coronawarn.analytics.services.ios;

import app.coronawarn.analytics.common.persistence.domain.ApiToken;
import app.coronawarn.analytics.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.analytics.common.protocols.AnalyticsSubmissionPayloadIOS;
import app.coronawarn.analytics.common.protocols.AuthIOS;
import app.coronawarn.analytics.common.protocols.Metrics;
import app.coronawarn.analytics.services.ios.controller.IosDeviceApiClient;
import app.coronawarn.analytics.services.ios.domain.IosDeviceData;
import app.coronawarn.analytics.services.ios.domain.IosDeviceDataUpdateRequest;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IosAuthenticationIntegrationTest {

  private static final String IOS_SERVICE_URL = "/version/v1/iOS/data";
  private static final String API_TOKEN = "API_TOKEN";
  private static final String DEVICE_TOKEN = "DEVICE_TOKEN";

  @Autowired
  TestRestTemplate testRestTemplate;

  @Autowired
  ApiTokenRepository apiTokenRepository;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");

  @BeforeEach
  void clearDatabase() {
    apiTokenRepository.deleteAll();
  }

  @Test
  public void submitDataErrorUpdatingPerDevicedata_rollback() {
    // Have no API Token YET
    // and a submission that correspond to per-device data that was last updated last month
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current month.
    OffsetDateTime now = OffsetDateTime.now();

    IosDeviceData data = buildIosDeviceData(now.minusMonths(1), true);
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(any())).thenReturn(data);
    doThrow(FeignException.class).when(iosDeviceApiClient).updatePerDeviceData(any());
    postSubmission(submissionPayloadIOS);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(API_TOKEN);

    assertThat(optionalApiToken.isPresent()).isEqualTo(false);
  }

  @Test
  public void submitData_updatePerDeviceData() {
    // Have no API Token YET
    // and a submission that correspond to per-device data that was last updated last month
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current month.
    OffsetDateTime now = OffsetDateTime.now();

    IosDeviceData data = buildIosDeviceData(now.minusMonths(1), true);
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);
    ArgumentCaptor<IosDeviceDataUpdateRequest> deviceTokenArgumentCaptor = ArgumentCaptor
        .forClass(IosDeviceDataUpdateRequest.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(any())).thenReturn(data);
    doNothing().when(iosDeviceApiClient).updatePerDeviceData(deviceTokenArgumentCaptor.capture());
    postSubmission(submissionPayloadIOS);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(API_TOKEN);

    assertThat(optionalApiToken.isPresent()).isEqualTo(true);
    assertThat(optionalApiToken.get().getExpirationDate()).isEqualTo(getLastDayOfMonth(OffsetDateTime.now()));
    assertThat(deviceTokenArgumentCaptor.getValue().isBit0()).isEqualTo(false);
    assertThat(deviceTokenArgumentCaptor.getValue().isBit1()).isEqualTo(false);
  }


  @Test
  public void submitDataApiTokenAlreadyUsed() {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token.

    // given
    IosDeviceData data = buildIosDeviceData(OffsetDateTime.now(), true);
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(any())).thenReturn(data);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIOS);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(API_TOKEN);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(optionalApiToken.isPresent()).isEqualTo(false);
  }


  @Test
  public void submitDataApiTokenExpired() {
    // Existing API Token that expired LAST month is compared against current timestamp
    // submission will fail because the API Token expired last month.

    // given
    OffsetDateTime now = OffsetDateTime.now();
    LocalDateTime expirationDate = getLastDayOfMonth(now.minusMonths(1));
    long timestamp = now.toInstant().getEpochSecond();
    apiTokenRepository.insert(API_TOKEN, expirationDate, timestamp, timestamp);
    IosDeviceData data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(any())).thenReturn(data);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIOS);

    // then
    Optional<ApiToken> apiToken = apiTokenRepository.findById(API_TOKEN);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(apiToken.isPresent()).isEqualTo(true);
    assertThat(apiToken.get().getExpirationDate()).isEqualTo(expirationDate);
  }


  @Test
  public void submitDataFailRetrievingPerDeviceData_badRequest() {
    when(iosDeviceApiClient.queryDeviceData(any())).thenThrow(FeignException.BadRequest.class);

    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIOS);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Test
  public void submitDataFailRetrievingPerDeviceData_internalServerError() {
    // Querying the apple device api returns a statuscode that is not 400 nor 200

    // given
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(any())).thenThrow(FeignException.class);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIOS);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

  }

  @Test
  public void submitDateInvalidPerDeviceData() {
    // Toy data contains invalid values for bot0 and bit1 (both have state 1)

    // given
    IosDeviceData data = buildIosDeviceData(OFFSET_DATE_TIME, false);
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(any())).thenReturn(data);
    doNothing().when(iosDeviceApiClient).updatePerDeviceData(any());
    ResponseEntity<Void> response = postSubmission(submissionPayloadIOS);

    // when
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private LocalDateTime getLastDayOfMonth(OffsetDateTime offsetDateTime) {
    return offsetDateTime
        .with(TemporalAdjusters.lastDayOfMonth()).truncatedTo(ChronoUnit.DAYS)
        .toLocalDateTime();
  }


  private IosDeviceData buildIosDeviceData(OffsetDateTime lastUpdated, boolean valid) {
    IosDeviceData data = new IosDeviceData();
    if (valid) {
      data.setBit0(true);
      data.setBit1(false);
    } else {
      data.setBit0(true);
      data.setBit1(true);
    }
    data.setLastUpdated(lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    return data;
  }

  private ResponseEntity<Void> postSubmission(
      AnalyticsSubmissionPayloadIOS submissionPayloadIOS) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    return testRestTemplate.exchange(IOS_SERVICE_URL, HttpMethod.POST,
        new HttpEntity<>(submissionPayloadIOS), Void.class,
        httpHeaders);

  }


  private AnalyticsSubmissionPayloadIOS buildSubmissionPayload(String apiToken) {
    AuthIOS authIOS = AuthIOS
        .newBuilder()
        .setApiToken(apiToken)
        .setDeviceToken(DEVICE_TOKEN)
        .build();
    Metrics metrics = Metrics.newBuilder()
        .build();
    AnalyticsSubmissionPayloadIOS submissionPayloadIOS = AnalyticsSubmissionPayloadIOS
        .newBuilder()
        .setAuthentication(authIOS)
        .setMetrics(metrics)
        .build();
    return submissionPayloadIOS;
  }
}
