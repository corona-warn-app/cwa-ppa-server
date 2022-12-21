package app.coronawarn.datadonation.common.persistence.domain;

import javax.validation.constraints.Size;

public class SrsOneTimePassword extends OneTimePassword {

  /**
   * No argument constructor.
   */
  public SrsOneTimePassword() {
  }

  /**
   * Constructs the {@link SrsOneTimePassword}.
   *
   * @param password The otp to store.
   */
  public SrsOneTimePassword(@Size(min = 36, max = 36) String password) {
    super(password);
  }
}
