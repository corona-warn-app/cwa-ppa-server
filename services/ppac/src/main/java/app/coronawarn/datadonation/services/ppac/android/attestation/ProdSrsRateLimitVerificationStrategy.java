package app.coronawarn.datadonation.services.ppac.android.attestation;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.service.AndroidIdService;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.DeviceQuoteExceeded;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import com.google.protobuf.ByteString;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.HexEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.apache.coyote.http11.Constants.a;

@Component
@Profile("!loadtest")
public class ProdSrsRateLimitVerificationStrategy implements SrsRateLimitVerificationStrategy {

    private final PpacConfiguration appParameters;

    @Autowired
    private AndroidIdService androidIdService;

    /**
     * Just constructs an instance.
     */
    public ProdSrsRateLimitVerificationStrategy(PpacConfiguration appParameters) {
        this.appParameters = appParameters;
    }

    /**
     * Verify that the given android id does not violate the rate limit.
     */
    public void validateSrsRateLimit(ByteString androidIdByteString, String pepper) {
        String pepperedAndroidId = calculatePepperedAndroidId(androidIdByteString, pepper);
        Optional<AndroidId> androidIdByPrimaryKey = androidIdService.getAndroidIdByPrimaryKey(pepperedAndroidId);
        if(androidIdByPrimaryKey.isPresent()) {
            AndroidId androidId = androidIdByPrimaryKey.get();
            Long lastUsedForSrsInMilliseconds = androidId.getLastUsedForSrs();
            Integer timeBetweenSubmissionsInDays = appParameters.getAndroid().getSrs().getSrsTimeBetweenSubmissionsInDays();
            isAndroidIdStillValid(lastUsedForSrsInMilliseconds, timeBetweenSubmissionsInDays);
        }


        throw new AndroidIdNotValid();
    }

    private void isAndroidIdStillValid(Long lastUsedForSrsInMilliseconds, Integer timeBetweenSubmissionsInDays) {
        long timeIntervalInSeconds = timeBetweenSubmissionsInDays * 24 * 3600;
        Instant expirationDate = Instant.ofEpochMilli(lastUsedForSrsInMilliseconds).plusSeconds(timeIntervalInSeconds);
        //TODO: is this condition correct, or do we need the opposite?
        if(expirationDate.isBefore(Instant.now())) {
            throw new DeviceQuoteExceeded();
        }
    }

    private String calculatePepperedAndroidId(ByteString androidId, String pepper) {
        try {
            byte[] hexedPepper = Hex.decodeHex(pepper);
            byte[] androidIdBytes = androidId.toByteArray();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write( hexedPepper );
            outputStream.write( androidIdBytes );

            byte concatenatedByteArray[] = outputStream.toByteArray( );
            //FIXME: no idea what is really required here.
        } catch (DecoderException | IOException e) {
            //FIXME: what do we actually best do here? Do we simply return a 500 error here, with a custom exception?
            throw new RuntimeException("Error during processing");
        }
        //FIXME: return the correct value, not null
        return null;
    }
}
