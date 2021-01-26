package app.coronawarn.datadonation.services.ppac.ios.exception;

public class EdusAlreadyAccessedException extends RuntimeException {

  public EdusAlreadyAccessedException(String msg) {
    super(msg);
  }
}
