package org.captcha.solver.repository;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.captcha.solver.CustomTestProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.captcha.solver.TestData.FILENAME;
import static org.captcha.solver.TestData.buildEntity;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestProfile(CustomTestProfile.class)
class CaptchaRepositoryIT {

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
  void shouldFindByFilename() {
    // when
    var actual = assertDoesNotThrow(() -> captchaRepository.findByFilename(FILENAME));

    // then
    assertTrue(actual.isPresent());
  }

  @Test
  void shouldFindRandom() {
    // when
    var actual = assertDoesNotThrow(() -> captchaRepository.findRandom());

    // then
    assertTrue(actual.isPresent());
  }

  @Test
  void shouldFindAllIds() {
    // when
    var actual = assertDoesNotThrow(() -> captchaRepository.findAllIds());

    // then
    assertEquals(1, actual.size());
  }

  @Test
  void shouldCountPredicted() {
    // when
    var actual = assertDoesNotThrow(() -> captchaRepository.countPredicted());

    // then
    assertEquals(1, actual);
  }

  @Test
  void shouldCountCorrectPredictions() {
    // when
    var actual = assertDoesNotThrow(() -> captchaRepository.countCorrectPredictions());

    // then
    assertEquals(1, actual);
  }

}
