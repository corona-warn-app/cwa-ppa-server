package app.coronawarn.datadonation.services.ppac.ios.exception;

public class DeviceBlockedException extends RuntimeException {

  public DeviceBlockedException() {
    super("PPAC failed due to blocked device");
  }
}
