package app.coronawarn.datadonation.services.ppac.ios.verification.errors;

public class DeviceBlocked extends RuntimeException {

  private static final long serialVersionUID = 3337613999147226824L;

  public DeviceBlocked() {
    super("PPAC failed due to blocked device");
  }
}
