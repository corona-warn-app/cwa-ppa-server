package app.coronawarn.datadonation.common.persistence.domain;

import jakarta.validation.constraints.Size;

public class ElsOneTimePassword extends OneTimePassword {

  /**
   * No argument constructor.
   */
  public ElsOneTimePassword() {
  }

  /**
   * Constructs the {@link ElsOneTimePassword}.
   *
   * @param password The otp to store.
   */
  public ElsOneTimePassword(@Size(min = 36, max = 36) String password) {
    super(password);
  }
}
