package org.captcha.solver.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.captcha.solver.TestData.LABEL;
import static org.captcha.solver.TestData.getSampleUri;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MultipartBodyTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @MethodSource("provideArguments")
  void shouldValidate(InputStream file, String filename, int expectedViolations) {
    // given
    var multipartBody = new MultipartBody();
    multipartBody.setFile(file);
    multipartBody.setFilename(filename);

    // when
    var actual = validator.validate(multipartBody);

    // then
    assertEquals(expectedViolations, actual.size());
  }

  private static Stream<Arguments> provideArguments() throws IOException {
    var path = Path.of(getSampleUri());
    var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
    return Stream.of(
        arguments(inputStream, LABEL, 0),
        arguments(inputStream, null, 1),
        arguments(inputStream, "", 1),
        arguments(inputStream, " ", 1),
        arguments(null, LABEL, 1),
        arguments(null, null, 2),
        arguments(null, "", 2),
        arguments(null, " ", 2)
    );
  }

}
