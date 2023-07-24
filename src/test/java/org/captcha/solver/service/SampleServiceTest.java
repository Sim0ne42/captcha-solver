package org.captcha.solver.service;

import org.captcha.solver.repository.CaptchaRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import static org.captcha.solver.TestData.buildEntity;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class SampleServiceTest {

  private MockitoSession mockitoSession;
  private CaptchaRepository captchaRepository;
  private SampleService underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    captchaRepository = mock(CaptchaRepository.class);
    underTest = new SampleService(captchaRepository);
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Test
  void shouldAddSample() {
    // given
    var entity = buildEntity();

    // when
    doNothing().when(captchaRepository).persist(entity);
    assertDoesNotThrow(() -> underTest.addSample(entity));

    // then
    verifyNoMoreInteractions(captchaRepository);
  }

  @Test
  void shouldCatchConstraintViolationException() {
    // given
    var entity = buildEntity();

    // when
    doThrow(ConstraintViolationException.class).when(captchaRepository).persist(entity);
    assertDoesNotThrow(() -> underTest.addSample(entity));

    // then
    verifyNoMoreInteractions(captchaRepository);
  }

  @Test
  void shouldThrowException() {
    // given
    var entity = buildEntity();

    // when
    doThrow(RuntimeException.class).when(captchaRepository).persist(entity);
    assertThrows(RuntimeException.class, () -> underTest.addSample(entity));

    // then
    verifyNoMoreInteractions(captchaRepository);
  }

}
