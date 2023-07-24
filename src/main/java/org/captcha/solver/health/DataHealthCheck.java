package org.captcha.solver.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.captcha.solver.repository.CaptchaRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Readiness
@ApplicationScoped
public class DataHealthCheck implements HealthCheck {

  private final int threshold;
  private final CaptchaRepository captchaRepository;

  public DataHealthCheck(@ConfigProperty(name = "accuracy.threshold", defaultValue = "80") int threshold,
      CaptchaRepository captchaRepository) {
    this.threshold = threshold;
    this.captchaRepository = captchaRepository;
  }

  @Override
  public HealthCheckResponse call() {
    var predicted = captchaRepository.countPredicted();

    var builder = HealthCheckResponse.named("Health check with data")
        .withData("Threshold", threshold + "%");

    if (predicted > 0) {
      var correctPredictions = captchaRepository.countCorrectPredictions();

      var accuracy = BigDecimal.valueOf(correctPredictions)
          .multiply(BigDecimal.valueOf(100))
          .divide(BigDecimal.valueOf(predicted), 2, RoundingMode.HALF_UP);

      builder.withData("Accuracy", accuracy + "%");
      return accuracy.longValue() < threshold ? builder.down().build() : builder.up().build();
    }

    return builder.up()
        .withData("Accuracy", "No data")
        .build();
  }

}
