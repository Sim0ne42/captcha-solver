package org.captcha.solver.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.captcha.solver.TestData.LABEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CaptchaVerificationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldValidate(Integer captchaId, String text, int expectedViolations) {
    // given
    var captchaVerification = new CaptchaVerification();
    captchaVerification.setCaptchaId(captchaId);
    captchaVerification.setText(text);

    // when
    var actual = validator.validate(captchaVerification);

    // then
    assertEquals(expectedViolations, actual.size());
  }

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
        arguments(1, LABEL, 0),
        arguments(null, LABEL, 1),
        arguments(-1, LABEL, 1),
        arguments(0, LABEL, 1),
        arguments(1, null, 1),
        arguments(1, "", 1),
        arguments(1, " ", 1),
        arguments(null, null, 2),
        arguments(-1, null, 2),
        arguments(0, null, 2),
        arguments(null, "", 2),
        arguments(-1, "", 2),
        arguments(0, "", 2),
        arguments(null, " ", 2),
        arguments(-1, " ", 2),
        arguments(0, " ", 2)
    );
  }

}
