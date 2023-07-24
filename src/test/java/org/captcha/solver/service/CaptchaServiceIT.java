package org.captcha.solver.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.captcha.solver.repository.CaptchaRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@Slf4j
class CaptchaServiceIT {

  @Inject
  CaptchaService captchaService;

  @Inject
  CaptchaRepository captchaRepository;

  @Test
  void addSamples() {
    assertDoesNotThrow(() -> captchaService.addSamples());
  }

  @Test
  void predictAll() {
    var predicted = 0;
    for (var id : captchaRepository.findAllIds()) {
      var predictedValue = assertDoesNotThrow(() -> captchaService.predict(id));
      assertNotNull(predictedValue);
      log.info("Number of predicted captcha: {}", ++predicted);
    }
    assertEquals(captchaRepository.count(), captchaRepository.countPredicted());
  }

}
