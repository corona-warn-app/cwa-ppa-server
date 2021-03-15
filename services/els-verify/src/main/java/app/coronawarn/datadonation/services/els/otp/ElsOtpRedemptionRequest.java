package app.coronawarn.datadonation.services.els.otp;

import javax.validation.constraints.Pattern;

public class ElsOtpRedemptionRequest {

  /**
   * UUID.
   */
  @Pattern(regexp = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")
  private String els;

  public String getEls() {
    return els;
  }

  public void setEls(String els) {
    this.els = els;
  }
}
