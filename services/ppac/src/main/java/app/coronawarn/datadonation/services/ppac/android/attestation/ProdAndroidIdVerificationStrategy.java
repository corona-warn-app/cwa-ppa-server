package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.springframework.util.ObjectUtils.isEmpty;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration.Android.Srs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProdAndroidIdVerificationStrategy implements AndroidIdVerificationStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProdAndroidIdVerificationStrategy.class);

  private final boolean requireAndroidIdSyntaxCheck;

  private final int minAndroidIdLength;

  private final int maxAndroidIdLength;

  /**
   * Just constructs an instance.
   */
  public ProdAndroidIdVerificationStrategy(final PpacConfiguration appParameters) {
    final Srs srs = appParameters.getAndroid().getSrs();
    requireAndroidIdSyntaxCheck = srs.getRequireAndroidIdSyntaxCheck();
    minAndroidIdLength = srs.getMinAndroidIdLength();
    maxAndroidIdLength = srs.getMaxAndroidIdLength();
  }

  /**
   * Verify that the given android id has the correct size.
   *
   * @param androidId to be validated.
   */
  @Override
  public void validateAndroidId(final byte[] androidId) {
    if (isEmpty(androidId)) {
      throw new MissingMandatoryAuthenticationFields("No Android ID received");
    }
    if (requireAndroidIdSyntaxCheck
        && (androidId.length < minAndroidIdLength || androidId.length > maxAndroidIdLength)) {
      LOGGER.debug("androidId.lenght = '{}'", androidId.length);
      throw new AndroidIdNotValid();
    }
  }
}
