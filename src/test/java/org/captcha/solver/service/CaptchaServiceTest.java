package org.captcha.solver.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.captcha.solver.domain.CaptchaVerification;
import org.captcha.solver.factory.CaptchaFactory;
import org.captcha.solver.repository.CaptchaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.captcha.solver.TestData.FILENAME;
import static org.captcha.solver.TestData.ID;
import static org.captcha.solver.TestData.LABEL;
import static org.captcha.solver.TestData.buildEntity;
import static org.captcha.solver.TestData.getSampleUri;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CaptchaServiceTest {

  private MockitoSession mockitoSession;
  private CaptchaRepository captchaRepository;
  private CaptchaFactory captchaFactory;
  private SampleService sampleService;
  private CaptchaService underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    captchaRepository = mock(CaptchaRepository.class);
    captchaFactory = mock(CaptchaFactory.class);
    sampleService = mock(SampleService.class);
    underTest = new CaptchaService(captchaRepository, captchaFactory, sampleService);
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Nested
  class GetRandom {

    @Test
    void shouldGetRandom() {
      // given
      var entity = buildEntity();
      entity.setId(ID);

      // when
      when(captchaRepository.findRandom()).thenReturn(Optional.of(entity));

      var actual = assertDoesNotThrow(() -> underTest.getRandom());

      // then
      assertNotNull(actual);
      assertEquals(entity.getId(), actual.getId());
      assertEquals(new String(entity.getImage()), actual.getBase64Image());
      assertEquals(entity.getFormat(), actual.getFormat());

      verifyNoMoreInteractions(captchaRepository);
      verifyNoInteractions(captchaFactory, sampleService);
    }

    @Test
    void shouldThrowNotFoundException() {
      // when
      when(captchaRepository.findRandom()).thenReturn(Optional.empty());

      var exception = assertThrows(NotFoundException.class,
          () -> underTest.getRandom());

      // then
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
      assertEquals("HTTP 404 Not Found", exception.getMessage());

      verifyNoMoreInteractions(captchaRepository);
      verifyNoInteractions(captchaFactory, sampleService);
    }
  }

  @Nested
  class Verify {

    @Test
    void shouldReturnTrue() {
      // given
      var verification = new CaptchaVerification();
      verification.setCaptchaId(ID);
      verification.setText(LABEL);
      var entity = buildEntity();

      // when
      when(captchaRepository.findByIdOptional(ID)).thenReturn(Optional.of(entity));

      var actual = assertDoesNotThrow(() -> underTest.verify(verification));

      // then
      assertTrue(actual);

      verifyNoMoreInteractions(captchaRepository);
      verifyNoInteractions(captchaFactory, sampleService);
    }

    @Test
    void shouldReturnFalse() {
      // given
      var verification = new CaptchaVerification();
      verification.setCaptchaId(ID);
      verification.setText("qwerty");
      var entity = buildEntity();

      // when
      when(captchaRepository.findByIdOptional(ID)).thenReturn(Optional.of(entity));

      var actual = assertDoesNotThrow(() -> underTest.verify(verification));

      // then
      assertFalse(actual);

      verifyNoMoreInteractions(captchaRepository);
      verifyNoInteractions(captchaFactory, sampleService);
    }

    @Test
    void shouldThrowNotFoundException() {
      // given
      var verification = new CaptchaVerification();
      verification.setCaptchaId(ID);
      verification.setText(LABEL);

      // when
      when(captchaRepository.findByIdOptional(ID)).thenReturn(Optional.empty());

      var exception = assertThrows(NotFoundException.class,
          () -> underTest.verify(verification));

      // then
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), exception.getResponse().getStatus());
      assertEquals("HTTP 404 Not Found", exception.getMessage());

      verifyNoMoreInteractions(captchaRepository);
      verifyNoInteractions(captchaFactory, sampleService);
    }

  }

  @Nested
  class Add {

    @Test
    void shouldAddCaptcha() throws IOException {
      // given
      var path = Path.of(getSampleUri());
      var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
      var entity = buildEntity();

      // when
      when(captchaRepository.findByFilename(FILENAME)).thenReturn(Optional.empty());
      when(captchaFactory.buildEntity(inputStream, FILENAME)).thenReturn(entity);
      doNothing().when(captchaRepository).persist(entity);

      assertDoesNotThrow(() -> underTest.add(inputStream, FILENAME));

      // then
      verifyNoMoreInteractions(captchaRepository, captchaFactory);
      verifyNoInteractions(sampleService);
    }

    @Test
    void shouldThrowBadRequestException() throws IOException {
      // given
      var path = Path.of(getSampleUri());
      var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));

      // when
      when(captchaRepository.findByFilename(FILENAME)).thenReturn(Optional.empty());
      when(captchaFactory.buildEntity(inputStream, FILENAME)).thenReturn(null);

      var exception = assertThrows(BadRequestException.class,
          () -> underTest.add(inputStream, FILENAME));

      // then
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus());
      assertEquals("HTTP 400 Bad Request", exception.getMessage());

      verifyNoMoreInteractions(captchaRepository, captchaFactory);
      verifyNoInteractions(sampleService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenCaptchaIsAlreadyPresent() throws IOException {
      // given
      var path = Path.of(getSampleUri());
      var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
      var entity = buildEntity();

      // when
      when(captchaRepository.findByFilename(FILENAME)).thenReturn(Optional.of(entity));

      var exception = assertThrows(BadRequestException.class,
          () -> underTest.add(inputStream, FILENAME));

      // then
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getResponse().getStatus());
      assertEquals("Filename already present", exception.getMessage());

      verifyNoMoreInteractions(captchaRepository);
      verifyNoInteractions(captchaFactory, sampleService);
    }
  }

}
