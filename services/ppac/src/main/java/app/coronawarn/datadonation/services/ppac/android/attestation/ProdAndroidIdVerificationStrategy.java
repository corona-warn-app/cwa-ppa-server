package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.springframework.util.ObjectUtils.isEmpty;

import app.coronawarn.datadonation.services.ppac.android.attestation.errors.AndroidIdNotValid;
import app.coronawarn.datadonation.services.ppac.android.attestation.errors.MissingMandatoryAuthenticationFields;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ProdAndroidIdVerificationStrategy implements AndroidIdVerificationStrategy {

  private final boolean requireAndroidIdSyntaxCheck;

  /**
   * Just constructs an instance.
   */
  public ProdAndroidIdVerificationStrategy(final PpacConfiguration appParameters) {
    this.requireAndroidIdSyntaxCheck = appParameters.getAndroid().getSrs().getRequireAndroidIdSyntaxCheck();
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
    if (requireAndroidIdSyntaxCheck && androidId.length != 8) {
      throw new AndroidIdNotValid();
    }
  }
}
