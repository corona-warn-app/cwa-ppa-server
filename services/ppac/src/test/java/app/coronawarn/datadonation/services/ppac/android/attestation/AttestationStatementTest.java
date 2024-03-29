package app.coronawarn.datadonation.services.ppac.android.attestation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.coronawarn.datadonation.services.ppac.android.attestation.AttestationStatement.EvaluationType;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AttestationStatementTest {

  @ParameterizedTest
  @MethodSource("expectedEvaluationTypesForStrings")
  void evaluationTypeShouldBeCorrectlyDetermined(String attestationEvType,
      List<EvaluationType> expectedEvaluationType, List<EvaluationType> notExpectedEvaluationType) {
    AttestationStatement underTest =
        new AttestationStatement("", 1, "", null, "", false, false, "", attestationEvType);
    expectedEvaluationType.forEach(evType -> assertTrue(underTest.isEvaluationTypeEqualTo(evType)));
    notExpectedEvaluationType
        .forEach(evType -> assertFalse(underTest.isEvaluationTypeEqualTo(evType)));
  }

  private static Stream<Arguments> expectedEvaluationTypesForStrings() {
    return Stream.of(
        Arguments.of("BASIC", List.of(EvaluationType.BASIC), List.of(EvaluationType.HARDWARE_BACKED)),
        Arguments.of("OTHER,BASIC", List.of(EvaluationType.BASIC), List.of(EvaluationType.HARDWARE_BACKED)),
        Arguments.of("    BASIC   ", List.of(EvaluationType.BASIC), List.of(EvaluationType.HARDWARE_BACKED)),
        Arguments.of("OTHER,BASIC,   OTHER", List.of(EvaluationType.BASIC), List.of(EvaluationType.HARDWARE_BACKED)),
        Arguments.of("OTHER", List.of(), List.of(EvaluationType.HARDWARE_BACKED, EvaluationType.BASIC)),
        Arguments.of("", List.of(), List.of(EvaluationType.BASIC, EvaluationType.HARDWARE_BACKED)),
        Arguments.of(null, List.of(), List.of(EvaluationType.BASIC, EvaluationType.HARDWARE_BACKED)),
        Arguments.of("HARDWARE_BACKED,BASIC,   OTHER",
            List.of(EvaluationType.BASIC, EvaluationType.HARDWARE_BACKED), List.of()));
  }

  @Test
  void getAdviceTest() {
    assertNull(new AttestationStatement().getAdvice());
  }

  @Test
  void getApkDigestSha256Test() {
    AttestationStatement attestationStatement = new AttestationStatement();
    assertThrows(NullPointerException.class, attestationStatement::getApkDigestSha256);
  }

  @Test
  void equalsTest() {
    AttestationStatement fixture = new AttestationStatement();
    assertEquals(fixture, fixture);

    assertNotEquals(null, fixture);
    assertEquals(fixture, new AttestationStatement());
  }

  @Test
  void hashCodeTest() {
    AttestationStatement fixture = new AttestationStatement();
    assertEquals(fixture.hashCode(), new AttestationStatement().hashCode());
  }
}
