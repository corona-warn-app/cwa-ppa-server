package app.coronawarn.analytics.services.ios.controller;

import app.coronawarn.analytics.common.protocols.IOSAnalyticsProto;
import app.coronawarn.analytics.services.ios.control.IosAnalyticsDataProcessor;
import app.coronawarn.analytics.services.ios.control.validation.ValidAnalyticsSubmissionPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/version/v1")
@Validated
public class IosController {

    private final IosAnalyticsDataProcessor iosAnalyticsDataProcessor;

    /**
     * The route to the submission endpoint (version agnostic).
     */
    public static final String SUBMISSION_ROUTE = "/iOS/data";
    private static final Logger logger = LoggerFactory.getLogger(IosController.class);

    IosController(IosAnalyticsDataProcessor iosAnalyticsDataProcessor) {
        this.iosAnalyticsDataProcessor = iosAnalyticsDataProcessor;
    }

    /**
     * Handles diagnosis key submission requests.
     *
     * @param exposureKeys The unmarshalled protocol buffers submission payload.
     * @return An empty response body.
     */
    @PostMapping(value = SUBMISSION_ROUTE)
    public DeferredResult<ResponseEntity<Void>> submitData(
            @RequestHeader("api_token") final String apiToken,
            @ValidAnalyticsSubmissionPayload @RequestBody IOSAnalyticsProto exposureKeys) {
        return buildRealDeferredResult(exposureKeys,apiToken);
    }

    private DeferredResult<ResponseEntity<Void>> buildRealDeferredResult(IOSAnalyticsProto submissionPayload, final String apiToken) {
        DeferredResult<ResponseEntity<Void>> deferredResult = new DeferredResult<>();
        iosAnalyticsDataProcessor.process(submissionPayload,apiToken);
        return deferredResult;
    }
}
