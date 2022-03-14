package app.coronawarn.datadonation.common.persistence.service;

import org.springframework.context.annotation.Profile;

@Profile("test")
public class DeleteSaltException extends RuntimeException {

  private static final long serialVersionUID = -6947727813948118963L;

  public DeleteSaltException(String salt) {
    super("Delete salt: " + salt + " from database failed. ");
  }

}
