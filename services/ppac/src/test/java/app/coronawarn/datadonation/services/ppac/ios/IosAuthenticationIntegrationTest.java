package app.coronawarn.datadonation.services.ppac.ios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.persistence.domain.ApiToken;
import app.coronawarn.datadonation.common.persistence.repository.ApiTokenRepository;
import app.coronawarn.datadonation.common.protocols.AuthIos;
import app.coronawarn.datadonation.common.protocols.Metrics;
import app.coronawarn.datadonation.common.protocols.SubmissionPayloadIos;
import app.coronawarn.datadonation.services.ppac.config.TestWebSecurityConfig;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataResponse;
import app.coronawarn.datadonation.services.ppac.ios.client.domain.PerDeviceDataUpdateRequest;
import app.coronawarn.datadonation.services.ppac.ios.utils.TimeUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import feign.FeignException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestWebSecurityConfig.class)
public class IosAuthenticationIntegrationTest {

  private static final String IOS_SERVICE_URL = "/version/v1/iOS/data";
  private static final String API_TOKEN = "API_TOKEN";
  private static final String DEVICE_TOKEN = "DEVICE_TOKEN";
  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.parse("2021-10-01T10:00:00+01:00");
  @Autowired
  TestRestTemplate testRestTemplate;
  @Autowired
  ApiTokenRepository apiTokenRepository;
  @Autowired
  TimeUtils timeUtils;
  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @BeforeEach
  void clearDatabase() {
    apiTokenRepository.deleteAll();
  }

  @Test
  public void submitDataEdusAlreadyAccessed() {
    // given
    // a valid api token that expires this month
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate expirationDate = timeUtils.getLastDayOfMonthFor(now, ZoneOffset.UTC);
    long timestamp = timeUtils.getEpochSecondFor(now);
    apiTokenRepository.insert(API_TOKEN, expirationDate, timestamp, timestamp);

    // and valid device data
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(data);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIos);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
  }

  @Test
  public void submitDataErrorUpdatingPerDevicedata_rollback() {
    // Have no API Token YET
    // and a submission that correspond to per-device data that was last updated last month
    // Per-Device Data should be updated and a new API Token should be created with expiration set to end of the current month.
    // After exception is thrown the db isertion should be rollbacked. So no API Token will be found.
    OffsetDateTime now = OffsetDateTime.now();

    PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(data);
    doThrow(FeignException.class).when(iosDeviceApiClient).updatePerDeviceData(anyString(), any());
    postSubmission(submissionPayloadIos);

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

    PerDeviceDataResponse data = buildIosDeviceData(now.minusMonths(1), true);
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);
    ArgumentCaptor<PerDeviceDataUpdateRequest> deviceTokenArgumentCaptor = ArgumentCaptor
        .forClass(PerDeviceDataUpdateRequest.class);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(data);
    doNothing().when(iosDeviceApiClient).updatePerDeviceData(anyString(), deviceTokenArgumentCaptor.capture());
    postSubmission(submissionPayloadIos);

    // then
    Optional<ApiToken> optionalApiToken = apiTokenRepository.findById(API_TOKEN);

    assertThat(optionalApiToken.isPresent()).isEqualTo(true);
    assertThat(optionalApiToken.get().getExpirationDate())
        .isEqualTo(timeUtils.getLastDayOfMonthFor(OffsetDateTime.now(), ZoneOffset.UTC));
    assertThat(deviceTokenArgumentCaptor.getValue().isBit0()).isEqualTo(false);
    assertThat(deviceTokenArgumentCaptor.getValue().isBit1()).isEqualTo(false);
  }

  @Test
  public void submitDataApiTokenAlreadyUsed() {
    // Toy ios device data that has last update NOW - this will be compared against current server time
    // so this means that someone altered the per device data already this month with an api token.

    // given
    PerDeviceDataResponse data = buildIosDeviceData(OffsetDateTime.now(), true);
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(data);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIos);

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
    LocalDate expirationDate = timeUtils.getLastDayOfMonthFor(now.minusMonths(1), ZoneOffset.UTC);
    long timestamp = timeUtils.getEpochSecondFor(now);

    apiTokenRepository.insert(API_TOKEN, expirationDate, timestamp, timestamp);
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, true);
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(data);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIos);

    // then
    Optional<ApiToken> apiToken = apiTokenRepository.findById(API_TOKEN);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(apiToken.isPresent()).isEqualTo(true);
    assertThat(apiToken.get().getExpirationDate()).isEqualTo(expirationDate);
  }

  @Test
  public void submitDataFailRetrievingPerDeviceData_badRequest() {
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(FeignException.BadRequest.class);

    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIos);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

  }

  @Test
  public void submitDataFailRetrievingPerDeviceData_internalServerError() {
    // Querying the apple device api returns a statuscode that is not 400 nor 200

    // given
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenThrow(FeignException.class);
    ResponseEntity<Void> response = postSubmission(submissionPayloadIos);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

  }

  @Test
  public void submitDateInvalidPerDeviceData() {
    // Toy data contains invalid values for bot0 and bit1 (both have state 1)

    // given
    PerDeviceDataResponse data = buildIosDeviceData(OFFSET_DATE_TIME, false);
    SubmissionPayloadIos submissionPayloadIos = buildSubmissionPayload(API_TOKEN);

    // when
    when(iosDeviceApiClient.queryDeviceData(anyString(), any())).thenReturn(data);
    doNothing().when(iosDeviceApiClient).updatePerDeviceData(anyString(), any());
    ResponseEntity<Void> response = postSubmission(submissionPayloadIos);

    // when
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  private LocalDateTime getLastDayOfMonth(OffsetDateTime offsetDateTime) {
    return offsetDateTime
        .with(TemporalAdjusters.lastDayOfMonth()).truncatedTo(ChronoUnit.DAYS)
        .toLocalDateTime();
  }

  private PerDeviceDataResponse buildIosDeviceData(OffsetDateTime lastUpdated, boolean valid) {
    PerDeviceDataResponse data = new PerDeviceDataResponse();
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
      SubmissionPayloadIos submissionPayloadIos) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.valueOf("application/x-protobuf"));
    return testRestTemplate.exchange(IOS_SERVICE_URL, HttpMethod.POST,
        new HttpEntity<>(submissionPayloadIos), Void.class,
        httpHeaders);

  }

  private SubmissionPayloadIos buildSubmissionPayload(String apiToken) {
    AuthIos authIos = AuthIos
        .newBuilder()
        .setApiToken(apiToken)
        .setDeviceToken(DEVICE_TOKEN)
        .build();
    Metrics metrics = Metrics.newBuilder()
        .build();
    SubmissionPayloadIos submissionPayloadIos = SubmissionPayloadIos
        .newBuilder()
        .setAuthentication(authIos)
        .setMetrics(metrics)
        .build();
    return submissionPayloadIos;
  }
}
