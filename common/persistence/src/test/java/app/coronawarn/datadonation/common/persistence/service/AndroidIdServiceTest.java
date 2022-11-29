package app.coronawarn.datadonation.common.persistence.service;

import static app.coronawarn.datadonation.common.persistence.service.AndroidIdService.pepper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class AndroidIdServiceTest {

  /**
   * Prints newly generated 'Pepper' which can be used as secret in Vault.
   *
   * @param args
   * @throws NoSuchAlgorithmException
   */
  public static void main(final String[] args) throws NoSuchAlgorithmException {
    final byte[] b16 = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(b16);
    final String pepper = HexFormat.of().formatHex(b16);
    System.out.println(pepper);
  }

  @Test
  final void testPepperByteArrayByteArray() {
    final String androidId = "FFF852E5419D7067";
    final String pepper = "7c92700f863098670f865d3151299543";
    assertEquals("P0eD7H3CP5/arItLUvEpXw5nYOR+SCBznLPl5xYscWY=",
        pepper(HexFormat.of().parseHex(androidId), HexFormat.of().parseHex(pepper)));
  }

  @Test
  final void testPepperByteArrayByteArrayRandom() throws NoSuchAlgorithmException {
    final byte[] b8 = new byte[8];
    SecureRandom.getInstanceStrong().nextBytes(b8);
    final String androidId = HexFormat.of().formatHex(b8);

    final byte[] b16 = new byte[16];
    SecureRandom.getInstanceStrong().nextBytes(b16);
    final String pepper = HexFormat.of().formatHex(b16);

    final String pepperedId = pepper(HexFormat.of().parseHex(androidId), HexFormat.of().parseHex(pepper));
    assertEquals(44, pepperedId.length());
    assertEquals('=', pepperedId.charAt(43));
  }
}
