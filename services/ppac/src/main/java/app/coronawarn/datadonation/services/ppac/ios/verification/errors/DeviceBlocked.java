package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceBlocked extends RuntimeException {

  public DeviceBlocked() {
    super("PPAC failed due to blocked device");
  }
}
