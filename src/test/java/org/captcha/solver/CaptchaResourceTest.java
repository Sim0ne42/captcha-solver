package org.captcha.solver;

import jakarta.ws.rs.core.Response;
import org.captcha.solver.domain.CaptchaVerification;
import org.captcha.solver.domain.MultipartBody;
import org.captcha.solver.dto.CaptchaDto;
import org.captcha.solver.service.CaptchaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.io.InputStream;

import static org.captcha.solver.TestData.FILENAME;
import static org.captcha.solver.TestData.FORMAT;
import static org.captcha.solver.TestData.ID;
import static org.captcha.solver.TestData.IMAGE;
import static org.captcha.solver.TestData.LABEL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CaptchaResourceTest {

  private MockitoSession mockitoSession;
  private CaptchaService captchaService;
  private CaptchaResource underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    captchaService = mock(CaptchaService.class);
    underTest = new CaptchaResource(captchaService);
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Nested
  class Random {

    @Test
    void shouldGetRandomCaptcha() {
      // given
      var dto = CaptchaDto.builder()
          .id(ID)
          .base64Image(new String(IMAGE))
          .format(FORMAT)
          .build();

      // when
      when(captchaService.getRandom()).thenReturn(dto);
      try (var actual = assertDoesNotThrow(() -> underTest.getRandomCaptcha())) {

        // then
        assertEquals(Response.Status.OK.getStatusCode(), actual.getStatus());
        verifyNoMoreInteractions(captchaService);
      }
    }
  }

  @Nested
  class Verify {

    @Test
    void shouldReturnOk() {
      // given
      var verification = new CaptchaVerification();
      verification.setCaptchaId(ID);
      verification.setText(LABEL);

      // when
      when(captchaService.verify(verification)).thenReturn(true);
      try (var actual = assertDoesNotThrow(() -> underTest.verifyCaptcha(verification))) {

        // then
        assertEquals(Response.Status.OK.getStatusCode(), actual.getStatus());
        verifyNoMoreInteractions(captchaService);
      }
    }

    @Test
    void shouldReturnForbidden() {
      // given
      var verification = new CaptchaVerification();
      verification.setCaptchaId(ID);
      verification.setText(LABEL);

      // when
      when(captchaService.verify(verification)).thenReturn(false);
      try (var actual = assertDoesNotThrow(() -> underTest.verifyCaptcha(verification))) {

        // then
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), actual.getStatus());
        verifyNoMoreInteractions(captchaService);
      }
    }
  }

  @Nested
  class Add {

    @Test
    void shouldAddCaptcha() {
      // given
      var inputStream = InputStream.nullInputStream();
      var body = new MultipartBody();
      body.setFile(inputStream);
      body.setFilename(FILENAME);

      // when
      doNothing().when(captchaService).add(inputStream, FILENAME);
      try (var actual = assertDoesNotThrow(() -> underTest.addCaptcha(body))) {

        // then
        assertEquals(Response.Status.OK.getStatusCode(), actual.getStatus());
        verifyNoMoreInteractions(captchaService);
      }
    }
  }

}
