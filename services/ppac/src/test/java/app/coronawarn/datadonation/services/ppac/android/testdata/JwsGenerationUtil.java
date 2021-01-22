package app.coronawarn.datadonation.services.ppac.android.testdata;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.util.encoders.Base64Encoder;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.api.client.testing.json.webtoken.TestCertificates.CertData;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Lists;
import com.google.api.client.util.SecurityUtils;
import com.google.api.client.util.StringUtils;

public class JwsGenerationUtil {

  /**
   * Test leaf certificate.
   *
   * <pre>
   * Issuer: CN=Root
   * Subject: C=US, ST=California, L=Mountain View, O=Google Inc., CN=foo.bar.com
   * </pre>
   */
  public static final CertData FOO_BAR_COM_CERT = 
      new CertData("-----BEGIN CERTIFICATE-----\n"
      + "MIIC6TCCAdECASowDQYJKoZIhvcNAQELBQAwDzENMAsGA1UEAwwEUm9vdDAeFw0x\n"
      + "NDExMTgxNjU0MDNaFw0zNDExMTMxNjU0MDNaMGYxCzAJBgNVBAYTAlVTMRMwEQYD\n"
      + "VQQIDApDYWxpZm9ybmlhMRYwFAYDVQQHDA1Nb3VudGFpbiBWaWV3MRQwEgYDVQQK\n"
      + "DAtHb29nbGUgSW5jLjEUMBIGA1UEAwwLZm9vLmJhci5jb20wggEiMA0GCSqGSIb3\n"
      + "DQEBAQUAA4IBDwAwggEKAoIBAQCzFVKJOkqTmyyjMHWBOrLdpYmc0EcvG3MohaV+\n"
      + "UJrVrI2SDykY8YWSkTKz9BKmF8HP/GjPPDs3184Cej9b1WeyvVB8Rj3guH3oL+sJ\n"
      + "T3u9V2y4zyo5xO6FWMBYEQ6X8DkGlYtTp5theYbRrXNELul4lF+LtHTCaAANRMkO\n"
      + "l0NEoLa6BRhOG68gFfIAxx5lT8REE9utvPuy+rCaBHnfHOPf8pn0LSvceBijSIFo\n"
      + "S3Y5crjPVjyiPAZUHWnHTFAilfHnpLBlGxpCylePQhMKrPcgvDoD9nd0LA6xYLF7\n"
      + "DPXXSa8FLO+fPV8CNJCAsFuq9Rlf2Tt3SjLtWRYuh5LuctP7AgMBAAEwDQYJKoZI\n"
      + "hvcNAQELBQADggEBAEsMABZl+8Rlk0hqBktsDurri4nF/07CnSBe/zUbTiYhMpr7\n"
      + "VRIDlHLoe5lslLilfXzvaymcMFeH1uBxNwhf7IO7WvIwQeUHSV+rHyNygTTieO0J\n"
      + "n8Hw+4SCohHAdMvD5uWEwn3Lv+W4y7OhaSbzlhVCVCnFLVKicBayUXHtdJXJICok\n"
      + "R4+h/WNM7g0iKThakZOyfb8h1phy7TMTVlPFKrcVDo5m9+GhtPC4PNjGLok6r/jx\n"
      + "9CIOCapIqi8fXJEOxKvilYeAYqfjWvhx00juEUBHrpCQ8wT4TA+LlI02cRz5rxW4\n"
      + "FQAz1NdoG9HZDZWa+NNFTZdAmtWPJMLd+8L8sl4=\n" 
      + "-----END CERTIFICATE-----");

  /**
   * A test JWS signature.
   *
   * <p>
   * The signed JSON is the following message:
   *
   * <pre>
   * {"foo":"bar"}
   * </pre>
   *
   * <p>
   * The message is signed using {@code FOO_BAR_COM_KEY}.
   */
  public static final String JWS_SIGNATURE =
      "eyJhbGciOiJSUzI1NiIsIng1YyI6WyJNSUlDNlRDQ0FkRUNBU293RFFZSktvWklo"
          + "dmNOQVFFTEJRQXdEekVOTUFzR0ExVUVBd3dFVW05dmREQWVGdzB4TkRFeE1UZ3hO"
          + "alUwTUROYUZ3MHpOREV4TVRNeE5qVTBNRE5hTUdZeEN6QUpCZ05WQkFZVEFsVlRN"
          + "Uk13RVFZRFZRUUlEQXBEWVd4cFptOXlibWxoTVJZd0ZBWURWUVFIREExTmIzVnVk"
          + "R0ZwYmlCV2FXVjNNUlF3RWdZRFZRUUtEQXRIYjI5bmJHVWdTVzVqTGpFVU1CSUdB"
          + "MVVFQXd3TFptOXZMbUpoY2k1amIyMHdnZ0VpTUEwR0NTcUdTSWIzRFFFQkFRVUFB"
          + "NElCRHdBd2dnRUtBb0lCQVFDekZWS0pPa3FUbXl5ak1IV0JPckxkcFltYzBFY3ZH"
          + "M01vaGFWK1VKclZySTJTRHlrWThZV1NrVEt6OUJLbUY4SFAvR2pQUERzMzE4NENl"
          + "ajliMVdleXZWQjhSajNndUgzb0wrc0pUM3U5VjJ5NHp5bzV4TzZGV01CWUVRNlg4"
          + "RGtHbFl0VHA1dGhlWWJSclhORUx1bDRsRitMdEhUQ2FBQU5STWtPbDBORW9MYTZC"
          + "UmhPRzY4Z0ZmSUF4eDVsVDhSRUU5dXR2UHV5K3JDYUJIbmZIT1BmOHBuMExTdmNl"
          + "QmlqU0lGb1MzWTVjcmpQVmp5aVBBWlVIV25IVEZBaWxmSG5wTEJsR3hwQ3lsZVBR"
          + "aE1LclBjZ3ZEb0Q5bmQwTEE2eFlMRjdEUFhYU2E4RkxPK2ZQVjhDTkpDQXNGdXE5"
          + "UmxmMlR0M1NqTHRXUll1aDVMdWN0UDdBZ01CQUFFd0RRWUpLb1pJaHZjTkFRRUxC"
          + "UUFEZ2dFQkFFc01BQlpsKzhSbGswaHFCa3RzRHVycmk0bkYvMDdDblNCZS96VWJU"
          + "aVloTXByN1ZSSURsSExvZTVsc2xMaWxmWHp2YXltY01GZUgxdUJ4TndoZjdJTzdX"
          + "dkl3UWVVSFNWK3JIeU55Z1RUaWVPMEpuOEh3KzRTQ29oSEFkTXZENXVXRXduM0x2"
          + "K1c0eTdPaGFTYnpsaFZDVkNuRkxWS2ljQmF5VVhIdGRKWEpJQ29rUjQraC9XTk03"
          + "ZzBpS1RoYWtaT3lmYjhoMXBoeTdUTVRWbFBGS3JjVkRvNW05K0dodFBDNFBOakdM"
          + "b2s2ci9qeDlDSU9DYXBJcWk4ZlhKRU94S3ZpbFllQVlxZmpXdmh4MDBqdUVVQkhy"
          + "cENROHdUNFRBK0xsSTAyY1J6NXJ4VzRGUUF6MU5kb0c5SFpEWldhK05ORlRaZEFt"
          + "dFdQSk1MZCs4TDhzbDQ9IiwiTUlJQzhUQ0NBZG1nQXdJQkFnSUpBTU5JMTVIckd5"
          + "bGtNQTBHQ1NxR1NJYjNEUUVCQ3dVQU1BOHhEVEFMQmdOVkJBTU1CRkp2YjNRd0ho"
          + "Y05NVFF4TVRFNE1UWTFOREF6V2hjTk16UXhNVEV6TVRZMU5EQXpXakFQTVEwd0N3"
          + "WURWUVFEREFSU2IyOTBNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1J"
          + "SUJDZ0tDQVFFQXplVU5jNGJTV0hoT1RVKzVNUS9sT21talFXcGZCaStGSnV4dm9l"
          + "T21Rd2k2ZnJQS0tzYUtLWUdmQ1RQbEtFMGRtckVQOTVibmkvcUw1eEFwUDE3b3Jq"
          + "VWU2S1J0SkF3Rk5JNUVaYWRJZmpiaC9xKzg1QzFDcDJCUzJZbXVaUXpYWkhQNjN5"
          + "eUJwMDVZY2JNS3dDQkhYYUFnWWJtVFRrKzQrMXBqTnBIUDZZaUYyZ0NQdlNmem9r"
          + "R3loYnZCcW5QYm5UZEk5dzZmak5CWUFici91Qk9UVTB2SzRrdHpsV2s1bHZzbTUx"
          + "ZTh2c0xTcVdob0hBRHEwQXJpQWVsVTRTSHNTQUNrUlVRU3hXVjBLNWh6VHY0ZWN2"
          + "Q2JHOWRza2lEQ3dXZyt1VFJTb0FGZVpPaE9OTDAwMHE3VmV5M0RaVGNMbDgvTzRO"
          + "UVZhWlI1aUFnVldsV2Nzd0lEQVFBQm8xQXdUakFkQmdOVkhRNEVGZ1FVc2ltbElS"
          + "RGNKUjBvZlI3b004S3dIRk9IK3NJd0h3WURWUjBqQkJnd0ZvQVVzaW1sSVJEY0pS"
          + "MG9mUjdvTThLd0hGT0grc0l3REFZRFZSMFRCQVV3QXdFQi96QU5CZ2txaGtpRzl3"
          + "MEJBUXNGQUFPQ0FRRUFXUWw4U21iUW9CVjN0ak9KOHpNbGNOMHhPUHBTU05ieDBn"
          + "N0VML2RRZ0pwZXQwTWNXNjJSSGxnUUFPS2JTM1BSZW8ybnNSQi9aUnlZRHU0aTEz"
          + "WkhaOGJNc0dPRVM0QlFwejEzbXRtWGc5UmhzWHFMMGVEWWZCY2pqdGxydVVieGhu"
          + "QUxwNFZOMXpWZHlXQVBDajBldTNNeHBnTVdjeW41MFFtaUpTai9FcXUvbExodmUv"
          + "d0t2akc1V2huVjh1UktSdUZiRmN0MERIQUhNblpxRkhjR1M1U28wY1luU2ZLNWZi"
          + "QlJOZWxHZmxocGJiUHAwVjBhWGlxaW5xRDBZZTNPYVpkRnErMnJQMW9DL2E1L091"
          + "NExzcFkzYjVvRDlyRU5keTdicTBLZXdQRnRnUHZVa0pySjNUemJpd3ZwZ2haN3pH"
          + "MjZibko1STd1YzR5MVZ1anFhT0E9PSJdfQ.eyJmb28iOiJiYXIifQ.eWzIsJF4PE"
          + "xQap9HK6Vlz8DGlgGwoiLCtyOEK0Bfu_yHTAZeApn5rh6Uzfx06Gv6eHdM34YL_t"
          + "gLRb4bjuZVA8xvQ9uHNs8UtpBIOiUcagzvtKyyfCofk5U5sNb54GgVVYxa6p4A1O"
          + "bdJv1jjlUOnzR8keX5LsAM4Ia7xeqiFh0GER4l0ulVChy_bSn0IeNiKFW7HKcxtc"
          + "GO_zZTtlv4HiifuyPSk_ar2IDX1w599KXniVcWkQ_W1zcp5YuPDw8mIQDVCH2uQY"
          + "7qs2ejdZj5LIgIz4CbQ0wg53rlwE7DDQM6MNUgZLnzNmMSMfFrpE7_PQyxe2qJCs" 
          + "ucHODzEHX4Tg";

  /**
   * Test CA Certificate.
   *
   * <pre>
   * Issuer: CN=Root
   * Subject: CN=Root
   * </pre>
   */
  public static final CertData CA_CERT = 
      new CertData("-----BEGIN CERTIFICATE-----\n"
      + "MIIC8TCCAdmgAwIBAgIJAMNI15HrGylkMA0GCSqGSIb3DQEBCwUAMA8xDTALBgNV\n"
      + "BAMMBFJvb3QwHhcNMTQxMTE4MTY1NDAzWhcNMzQxMTEzMTY1NDAzWjAPMQ0wCwYD\n"
      + "VQQDDARSb290MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeUNc4bS\n"
      + "WHhOTU+5MQ/lOmmjQWpfBi+FJuxvoeOmQwi6frPKKsaKKYGfCTPlKE0dmrEP95bn\n"
      + "i/qL5xApP17orjUe6KRtJAwFNI5EZadIfjbh/q+85C1Cp2BS2YmuZQzXZHP63yyB\n"
      + "p05YcbMKwCBHXaAgYbmTTk+4+1pjNpHP6YiF2gCPvSfzokGyhbvBqnPbnTdI9w6f\n"
      + "jNBYAbr/uBOTU0vK4ktzlWk5lvsm51e8vsLSqWhoHADq0AriAelU4SHsSACkRUQS\n"
      + "xWV0K5hzTv4ecvCbG9dskiDCwWg+uTRSoAFeZOhONL000q7Vey3DZTcLl8/O4NQV\n"
      + "aZR5iAgVWlWcswIDAQABo1AwTjAdBgNVHQ4EFgQUsimlIRDcJR0ofR7oM8KwHFOH\n"
      + "+sIwHwYDVR0jBBgwFoAUsimlIRDcJR0ofR7oM8KwHFOH+sIwDAYDVR0TBAUwAwEB\n"
      + "/zANBgkqhkiG9w0BAQsFAAOCAQEAWQl8SmbQoBV3tjOJ8zMlcN0xOPpSSNbx0g7E\n"
      + "L/dQgJpet0McW62RHlgQAOKbS3PReo2nsRB/ZRyYDu4i13ZHZ8bMsGOES4BQpz13\n"
      + "mtmXg9RhsXqL0eDYfBcjjtlruUbxhnALp4VN1zVdyWAPCj0eu3MxpgMWcyn50Qmi\n"
      + "JSj/Equ/lLhve/wKvjG5WhnV8uRKRuFbFct0DHAHMnZqFHcGS5So0cYnSfK5fbBR\n"
      + "NelGflhpbbPp0V0aXiqinqD0Ye3OaZdFq+2rP1oC/a5/Ou4LspY3b5oD9rENdy7b\n"
      + "q0KewPFtgPvUkJrJ3TzbiwvpghZ7zG26bnJ5I7uc4y1VujqaOA==\n" 
      + "-----END CERTIFICATE-----");

  public static JsonWebSignature createJsonWebSignature(Map<String, String> payloadValues) throws IOException {
    JsonWebSignature.Header header = new JsonWebSignature.Header();
    header.setAlgorithm("RS256");
    List<String> certificates = Lists.newArrayList();
    certificates.add(FOO_BAR_COM_CERT.getBase64Der());
    certificates.add(CA_CERT.getBase64Der());
    header.setX509Certificates(certificates);
    JsonWebToken.Payload payload = new JsonWebToken.Payload();
    payloadValues.forEach( (k,v) ->  payload.set(k, v));
    int firstDot = JWS_SIGNATURE.indexOf('.');
    int secondDot = JWS_SIGNATURE.indexOf('.', firstDot + 1);
    byte[] signatureBytes = Base64.decodeBase64(JWS_SIGNATURE.substring(secondDot + 1));
    byte[] signedContentBytes = StringUtils.getBytesUtf8(JWS_SIGNATURE.substring(0, secondDot));
    JsonWebSignature signature =
        new JsonWebSignature(header, payload, signatureBytes, signedContentBytes);
    return signature;
  }
  
  public static String createCompactSerializedJws(Map<String, String> payloadValues) throws IOException {
    JsonWebSignature jws = createJsonWebSignature(payloadValues);
    GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
    String content =
        Base64.encodeBase64URLSafeString(gsonFactory.toByteArray(jws.getHeader()))
            + "."
            + Base64.encodeBase64URLSafeString(gsonFactory.toByteArray(jws.getPayload()));
    return content + "." + Base64.encodeBase64URLSafeString(jws.getSignatureBytes());
  }
}
