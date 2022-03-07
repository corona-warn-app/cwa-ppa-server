package app.coronawarn.datadonation.services.ppac.android.controller;

public class SaltNotFoundException extends RuntimeException {

  public SaltNotFoundException() {
    super("Salt not found");
  }

}
