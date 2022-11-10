package app.coronawarn.datadonation.common.persistence.service;

import static app.coronawarn.datadonation.common.persistence.service.AndroidIdService.pepper;
import static com.google.protobuf.ByteString.copyFrom;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.protobuf.ByteString;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class AndroidIdServiceTest {

  @Test
  final void testPepperByteArrayByteArray() {
    final String androidId = "FFF852E5419D7067";
    final String pepper = "7c92700f863098670f865d3151299543";
    assertEquals("P0eD7H3CP5/arItLUvEpXw5nYOR+SCBznLPl5xYscWY=",
        pepper(HexFormat.of().parseHex(androidId), HexFormat.of().parseHex(pepper)));
  }

  @Test
  final void testPepperByteStringString() {
    final ByteString androidId = copyFrom(HexFormat.of().parseHex("FFF852E5419D7067"));
    final String pepper = "7c92700f863098670f865d3151299543";
    assertEquals("P0eD7H3CP5/arItLUvEpXw5nYOR+SCBznLPl5xYscWY=", pepper(androidId, pepper));
  }
}
