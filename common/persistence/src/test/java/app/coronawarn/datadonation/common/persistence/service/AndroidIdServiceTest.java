package app.coronawarn.datadonation.common.persistence.service;

import static app.coronawarn.datadonation.common.persistence.service.AndroidIdService.pepper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import app.coronawarn.datadonation.common.persistence.domain.AndroidId;
import app.coronawarn.datadonation.common.persistence.repository.AndroidIdRepositoryTest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
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

  @Autowired
  AndroidIdService androidIdService;

  @Test
  public void testInsertAndroidId() {
    final byte[] id = new byte[8];
    new Random().nextBytes(id);

    final byte[] pepper = new byte[8];
    new Random().nextBytes(pepper);
    final String pepperedId = pepper(id, pepper);

    androidIdService.upsertAndroidId(id, 10, pepper);

    final Optional<AndroidId> androidIdByPrimaryKey = androidIdService.getAndroidIdByPrimaryKey(pepperedId);
    Assertions.assertFalse(androidIdByPrimaryKey.isEmpty());
    Assertions.assertEquals(pepperedId, androidIdByPrimaryKey.get().getId());
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

  @Test
  public void testUpdateAndroidId() {
    final byte[] id = new byte[8];
    new Random().nextBytes(id);

    final byte[] pepper = new byte[8];
    new Random().nextBytes(pepper);
    final String pepperedId = pepper(id, pepper);

    final AndroidId androidId = AndroidIdRepositoryTest.newAndroidId();
    final Long expirationDate = Instant.now().toEpochMilli();
    androidId.setExpirationDate(expirationDate);
    androidIdService.upsertAndroidId(id, 10, pepper);

    final Optional<AndroidId> androidIdByPrimaryKey = androidIdService.getAndroidIdByPrimaryKey(pepperedId);
    Assertions.assertFalse(androidIdByPrimaryKey.isEmpty());
    final Long lastUsedSrsOriginal = androidIdByPrimaryKey.get().getLastUsedSrs();

    // update the record
    androidIdService.upsertAndroidId(id, 10, pepper);
    final Optional<AndroidId> androidIdByPrimaryKeyUpdated = androidIdService.getAndroidIdByPrimaryKey(pepperedId);
    Assertions.assertFalse(androidIdByPrimaryKeyUpdated.isEmpty());
    Assertions.assertEquals(pepperedId, androidIdByPrimaryKey.get().getId());
    // since the 'last used for SRS' date is set when the record is saved, the new value must be greater the old one
    Assertions.assertTrue(androidIdByPrimaryKeyUpdated.get().getLastUsedSrs() > lastUsedSrsOriginal);
  }
}
