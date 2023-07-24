package org.captcha.solver.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.captcha.solver.entity.CaptchaEntity;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CaptchaRepository implements PanacheRepositoryBase<CaptchaEntity, Integer> {

  public Optional<CaptchaEntity> findByFilename(String filename) {
    return find("filename", filename).firstResultOptional();
  }

  public Optional<CaptchaEntity> findRandom() {
    return find("#CaptchaEntity.findRandom").firstResultOptional();
  }

  public List<Integer> findAllIds() {
    return find("#CaptchaEntity.findAllIds").project(Integer.class).list();
  }

  public long countPredicted() {
    return count("#CaptchaEntity.countPredicted");
  }

  public long countCorrectPredictions() {
    return count("#CaptchaEntity.countCorrectPredictions");
  }

}
