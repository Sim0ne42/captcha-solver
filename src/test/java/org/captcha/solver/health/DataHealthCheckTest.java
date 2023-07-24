package org.captcha.solver.health;

import org.captcha.solver.repository.CaptchaRepository;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockitoSession;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DataHealthCheckTest {

  private static final int THRESHOLD = 80;

  private MockitoSession mockitoSession;
  private CaptchaRepository captchaRepository;
  private DataHealthCheck underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    captchaRepository = mock(CaptchaRepository.class);
    underTest = new DataHealthCheck(THRESHOLD, captchaRepository);
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Test
  void testHealthCheck() {
    // when
    when(captchaRepository.countPredicted()).thenReturn(1000L);
    when(captchaRepository.countCorrectPredictions()).thenReturn(1000L);

    var actual = assertDoesNotThrow(() -> underTest.call());

    // then
    assertEquals("Health check with data", actual.getName());
    assertEquals(HealthCheckResponse.Status.UP, actual.getStatus());
    assertTrue(actual.getData().isPresent());
    assertEquals("100.00%", actual.getData().get().get("Accuracy"));
    assertEquals(THRESHOLD + "%", actual.getData().get().get("Threshold"));

    verifyNoMoreInteractions(captchaRepository);
  }

  @Test
  void testHealthCheckWhenThereIsNoData() {
    // when
    when(captchaRepository.countPredicted()).thenReturn(0L);

    var actual = assertDoesNotThrow(() -> underTest.call());

    // then
    assertEquals("Health check with data", actual.getName());
    assertEquals(HealthCheckResponse.Status.UP, actual.getStatus());
    assertTrue(actual.getData().isPresent());
    assertEquals("No data", actual.getData().get().get("Accuracy"));
    assertEquals(THRESHOLD + "%", actual.getData().get().get("Threshold"));

    verifyNoMoreInteractions(captchaRepository);
  }

}
