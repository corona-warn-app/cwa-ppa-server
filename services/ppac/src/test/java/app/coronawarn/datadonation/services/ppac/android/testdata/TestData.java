package app.coronawarn.datadonation.services.ppac.android.testdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestData {

  public static String loadJwsWithExpiredCertificates() throws IOException {
    InputStream fileStream = TestData.class.getResourceAsStream("/jwsSamples/invalid_samples.properties");
    Properties properties = new Properties();
    properties.load(fileStream);
    return (String) properties.get("expiredCertificates");
  }

}
