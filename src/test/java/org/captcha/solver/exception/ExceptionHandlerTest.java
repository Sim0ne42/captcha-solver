package org.captcha.solver.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockitoSession;

class ExceptionHandlerTest {

  private MockitoSession mockitoSession;
  private ExceptionHandler underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    underTest = new ExceptionHandler();
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Test
  void shouldHandleBadRequestException() {
    // given
    var message = "message";
    var exception = new BadRequestException(message);

    // when
    try (var actual = assertDoesNotThrow(() -> underTest.toResponse(exception))) {

      // then
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), actual.getStatus());
      assertEquals(message, actual.getEntity());
    }
  }

  @Test
  void shouldHandleNotFoundException() {
    // given
    var message = "message";
    var exception = new NotFoundException(message);

    // when
    try (var actual = assertDoesNotThrow(() -> underTest.toResponse(exception))) {

      // then
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), actual.getStatus());
      assertEquals(message, actual.getEntity());
    }
  }

  @Test
  void shouldHandleNotAllowedException() {
    // given
    var exception = new NotAllowedException(Response.status(Response.Status.METHOD_NOT_ALLOWED).build());

    // when
    try (var actual = assertDoesNotThrow(() -> underTest.toResponse(exception))) {

      // then
      assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), actual.getStatus());
      assertEquals("HTTP 405 Method Not Allowed", actual.getEntity());
    }
  }

  @Test
  void shouldHandleException() {
    // given
    var exception = new RuntimeException();

    // when
    try (var actual = assertDoesNotThrow(() -> underTest.toResponse(exception))) {

      // then
      assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actual.getStatus());
      assertNull(actual.getEntity());
    }
  }

}
