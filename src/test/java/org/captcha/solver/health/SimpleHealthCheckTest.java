package org.captcha.solver.health;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockitoSession;

class SimpleHealthCheckTest {

  private MockitoSession mockitoSession;
  private SimpleHealthCheck underTest;

  @BeforeEach
  void setup() {
    mockitoSession = mockitoSession().strictness(Strictness.STRICT_STUBS).startMocking();
    underTest = new SimpleHealthCheck();
  }

  @AfterEach
  void tearDown() {
    mockitoSession.finishMocking();
  }

  @Test
  void testHealthCheck() {
    // when
    var actual = assertDoesNotThrow(() -> underTest.call());

    // then
    assertEquals("Simple health check", actual.getName());
    assertEquals(HealthCheckResponse.Status.UP, actual.getStatus());
    assertTrue(actual.getData().isEmpty());
  }

}
