package org.captcha.solver.factory;

import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.captcha.solver.TestData.FILENAME;
import static org.captcha.solver.TestData.FORMAT;
import static org.captcha.solver.TestData.HEIGHT;
import static org.captcha.solver.TestData.LABEL;
import static org.captcha.solver.TestData.WIDTH;
import static org.captcha.solver.TestData.getSampleUri;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.when;

class CaptchaFactoryTest {

  private MockitoSession mockitoSession;
  private Set<String> supportedFormats;
  private CaptchaFactory underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    supportedFormats = mock(Set.class);
    lenient().when(supportedFormats.contains(anyString())).thenReturn(true);
    underTest = new CaptchaFactory(supportedFormats);
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Nested
  class BuildCaptchaSample {

    @Test
    void shouldBuildEntity() {
      // given
      var path = Path.of(getSampleUri());

      // when
      var actual = assertDoesNotThrow(() -> underTest.buildEntity(path));

      // then
      assertNotNull(actual);
      assertTrue(actual.getImage().length > 0);
      assertEquals(LABEL, actual.getLabel());
      assertEquals(FILENAME, actual.getFilename());
      assertEquals(FORMAT, actual.getFormat());
      assertEquals(WIDTH, actual.getWidth());
      assertEquals(HEIGHT, actual.getHeight());
      assertTrue(actual.getSample());
    }

    @Test
    void shouldCatchIOException() {
      // given
      var path = Path.of(getSampleUri());

      // when
      try (var files = mockStatic(Files.class)) {
        files.when(() -> Files.readAllBytes(path)).thenThrow(IOException.class);
        var actual = assertDoesNotThrow(() -> underTest.buildEntity(path));

        // then
        assertNull(actual);
      }
    }

    @Test
    void shouldThrowBadRequestException() {
      // given
      var path = Path.of(getSampleUri());

      // when
      when(supportedFormats.contains(anyString())).thenReturn(false);
      var exception = assertThrows(BadRequestException.class,
          () -> underTest.buildEntity(path));

      // then
      assertEquals("Unsupported format", exception.getMessage());
    }
  }

  @Nested
  class BuildCaptcha {

    @Test
    void shouldBuildEntity() throws IOException {
      // given
      var path = Path.of(getSampleUri());
      var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));

      // when
      var actual = assertDoesNotThrow(() -> underTest.buildEntity(inputStream, FILENAME));

      // then
      assertNotNull(actual);
      assertTrue(actual.getImage().length > 0);
      assertEquals(LABEL, actual.getLabel());
      assertEquals(FILENAME, actual.getFilename());
      assertEquals(FORMAT, actual.getFormat());
      assertEquals(WIDTH, actual.getWidth());
      assertEquals(HEIGHT, actual.getHeight());
      assertFalse(actual.getSample());
    }

    @Test
    void shouldCatchIOException() throws IOException {
      // given
      var inputStream = mock(InputStream.class);

      // when
      when(inputStream.readAllBytes()).thenThrow(IOException.class);
      var actual = assertDoesNotThrow(() -> underTest.buildEntity(inputStream, FILENAME));

      // then
      assertNull(actual);
    }

    @Test
    void shouldCatchImageIOException() throws IOException {
      // given
      var path = Path.of(getSampleUri());
      var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));

      // when
      try (var imageIO = mockStatic(ImageIO.class)) {
        imageIO.when(() -> ImageIO.read(inputStream)).thenThrow(IOException.class);
        var actual = assertDoesNotThrow(() -> underTest.buildEntity(inputStream, FILENAME));

        // then
        assertNull(actual);
      }
    }

    @Test
    void shouldThrowBadRequestException() throws IOException {
      // given
      var path = Path.of(getSampleUri());
      var inputStream = new ByteArrayInputStream(Files.readAllBytes(path));
      var filename = "2b827..png";

      // when
      var exception = assertThrows(BadRequestException.class,
          () -> underTest.buildEntity(inputStream, filename));

      // then
      assertEquals("Incorrect filename", exception.getMessage());
    }
  }

}
