package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class ApkPackageNameNotAllowed extends RuntimeException {

  private static final long serialVersionUID = 8772200600466947124L;

  public ApkPackageNameNotAllowed(String apkPackageName) {
    super("APK package not allowed: " + apkPackageName);
  }
}
