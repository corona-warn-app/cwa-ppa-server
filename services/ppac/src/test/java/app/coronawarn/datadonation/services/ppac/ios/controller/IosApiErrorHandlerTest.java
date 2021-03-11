package app.coronawarn.datadonation.services.ppac.ios.controller;

import static app.coronawarn.datadonation.common.config.UrlConstants.DATA;
import static app.coronawarn.datadonation.common.config.UrlConstants.IOS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.coronawarn.datadonation.common.config.SecurityLogger;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataIOS;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindow;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPAExposureWindowInfectiousness;
import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPANewExposureWindow;
import app.coronawarn.datadonation.common.utils.TimeUtils;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class IosApiErrorHandlerTest {

  private MockMvc mockMvc;

  @Mock
  private IosController iosController;

  private SecurityLogger logger;

  @Before
  public void setup() {
    openMocks(this);
    logger = mock(SecurityLogger.class);
    this.mockMvc = MockMvcBuilders.standaloneSetup(iosController)
        .setControllerAdvice(new IosApiErrorHandler(logger))
        .build();
  }

  @Test
  public void checkUnexpectedExceptionsAreCaughtAndStatusCode500IsReturnedInResponse() throws Exception {
    doThrow(RuntimeException.class).when(logger).securityWarn(any());
    doThrow(RuntimeException.class).when(logger).error(any());
    doThrow(RuntimeException.class).when(iosController).submitData(anyBoolean(), any());

    final Long epochSecondForNow = TimeUtils.getEpochSecondForNow();
    LocalDate now = TimeUtils.getLocalDateFor(epochSecondForNow);
    final PPAExposureWindow ppaExposureWindow = PPAExposureWindow
        .newBuilder()
        .setCalibrationConfidence(1)
        .setInfectiousness(PPAExposureWindowInfectiousness.INFECTIOUSNESS_HIGH)
        .setDate(epochSecondForNow)
        .build();

    final PPANewExposureWindow ppaNewExposureWindow = PPANewExposureWindow
        .newBuilder()
        .setExposureWindow(ppaExposureWindow)
        .build();

    final PPADataIOS payload = PPADataIOS.newBuilder()
        .addNewExposureWindows(ppaNewExposureWindow).build();

    MockHttpServletRequestBuilder request = post(IOS + DATA);
    request.contentType(MediaType.valueOf("application/x-protobuf"));
    request.header("cwa-ppac-ios-accept-api-token", "true");
    request.content(payload.toByteArray());

    mockMvc.perform(request).andExpect(status().isInternalServerError());
  }
}
