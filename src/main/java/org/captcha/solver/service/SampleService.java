package org.captcha.solver.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.captcha.solver.entity.CaptchaEntity;
import org.captcha.solver.repository.CaptchaRepository;
import org.hibernate.exception.ConstraintViolationException;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SampleService {

  private final CaptchaRepository captchaRepository;

  @Transactional
  public void addSample(CaptchaEntity entity) {
    try {
      captchaRepository.persist(entity);
      log.info("Sample successfully added: {}", entity.getFilename());
    } catch (ConstraintViolationException e) {
      log.warn("Sample already present: {}", entity.getFilename());
    }
  }

}
