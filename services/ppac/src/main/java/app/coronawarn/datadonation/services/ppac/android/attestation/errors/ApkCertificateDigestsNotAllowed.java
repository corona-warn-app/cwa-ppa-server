package app.coronawarn.datadonation.services.ppac.android.attestation.errors;

public class ApkCertificateDigestsNotAllowed extends RuntimeException {

  private static final long serialVersionUID = 7913843985526360886L;

  public ApkCertificateDigestsNotAllowed() {
    super("APK Certificate Digest Sha256 received which is not part of allowed list");
  }
}
