package org.captcha.solver;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.captcha.solver.domain.CaptchaVerification;
import org.captcha.solver.dto.CaptchaDto;
import org.captcha.solver.repository.CaptchaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.captcha.solver.TestData.FILENAME;
import static org.captcha.solver.TestData.LABEL;
import static org.captcha.solver.TestData.buildEntity;
import static org.captcha.solver.TestData.getSampleUri;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@QuarkusTest
@TestProfile(CustomTestProfile.class)
class CaptchaResourceIT {

  @Inject
  CaptchaRepository captchaRepository;

  @BeforeEach
  @Transactional
  void setup() {
    captchaRepository.persist(buildEntity());
  }

  @AfterEach
  @Transactional
  void tearDown() {
    captchaRepository.deleteAll();
  }

  @Test
  void testGetRandomCaptcha() {
    var actual = given()
        .when().get("/captcha/random")
        .then()
        .statusCode(HttpResponseStatus.OK.code())
        .contentType(MediaType.APPLICATION_JSON)
        .extract()
        .body()
        .as(CaptchaDto.class);

    assertNotNull(actual);
  }

  @ParameterizedTest
  @MethodSource("provideTextAndExpectedStatusCode")
  void testVerifyCaptcha(String text, int statusCode) {
    var body = new CaptchaVerification();
    body.setCaptchaId(captchaRepository.findAllIds().get(0));
    body.setText(text);

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .when().post("/captcha/verify")
        .then()
        .statusCode(statusCode)
        .body(is(""));
  }

  private static Stream<Arguments> provideTextAndExpectedStatusCode() {
    return Stream.of(
        arguments(LABEL, Response.Status.OK.getStatusCode()),
        arguments("qwerty", Response.Status.FORBIDDEN.getStatusCode())
    );
  }

  @ParameterizedTest
  @MethodSource("provideIdAndText")
  void testVerifyCaptchaKo(Integer id, String text) {
    var body = new CaptchaVerification();
    body.setCaptchaId(id);
    body.setText(text);

    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .when().post("/captcha/verify")
        .then()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(notNullValue());
  }

  private static Stream<Arguments> provideIdAndText() {
    return Stream.of(
        arguments(null, LABEL),
        arguments(0, LABEL),
        arguments(1, null),
        arguments(1, ""),
        arguments(1, " ")
    );
  }

  @Test
  void testAddCaptcha() {
    tearDown();

    given()
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .multiPart("file", new File(getSampleUri()))
        .multiPart("filename", FILENAME)
        .when().post("/captcha/add")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .body(is(""));

    assertEquals(1, captchaRepository.count());
  }

  @ParameterizedTest
  @MethodSource("provideFileAndFilename")
  void testAddCaptchaKo(File file, String filename) {
    tearDown();

    var specification = given()
        .contentType(MediaType.MULTIPART_FORM_DATA);
    Optional.ofNullable(file).ifPresent(it -> specification.multiPart("file", file));
    Optional.ofNullable(filename).ifPresent(it -> specification.multiPart("filename", filename));
    specification
        .when().post("/captcha/add")
        .then()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(notNullValue());

    assertEquals(0, captchaRepository.count());
  }

  private static Stream<Arguments> provideFileAndFilename() {
    var file = new File(getSampleUri());
    return Stream.of(
        arguments(null, LABEL),
        arguments(file, null),
        arguments(file, ""),
        arguments(file, " ")
    );
  }

}
