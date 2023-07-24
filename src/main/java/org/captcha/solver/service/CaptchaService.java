package org.captcha.solver.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.captcha.solver.domain.CaptchaVerification;
import org.captcha.solver.dto.CaptchaDto;
import org.captcha.solver.factory.CaptchaFactory;
import org.captcha.solver.repository.CaptchaRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.captcha.solver.exception.ExceptionHandler.ERROR_MESSAGE;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class CaptchaService {

  private final CaptchaRepository captchaRepository;
  private final CaptchaFactory captchaFactory;
  private final SampleService sampleService;

  public CaptchaDto getRandom() {
    return captchaRepository.findRandom()
        .map(entity -> CaptchaDto.builder()
            .id(entity.getId())
            .base64Image(new String(entity.getImage()))
            .format(entity.getFormat())
            .build())
        .orElseThrow(() -> {
          log.warn("No captcha found");
          return new NotFoundException();
        });
  }

  @Transactional
  public boolean verify(CaptchaVerification verification) {
    return captchaRepository.findByIdOptional(verification.getCaptchaId())
        .map(entity -> entity.getLabel().equals(verification.getText()))
        .orElseThrow(() -> {
          log.warn("Captcha with id {} not found", verification.getCaptchaId());
          return new NotFoundException();
        });
  }

  @Transactional
  public void add(InputStream inputStream, String filename) {
    if (captchaRepository.findByFilename(filename).isPresent()) {
      var message = "Filename already present";
      log.warn(message + ": {}", filename);
      throw new BadRequestException(message);
    }
    Optional.ofNullable(captchaFactory.buildEntity(inputStream, filename))
        .ifPresentOrElse(captchaRepository::persist, () -> {
          throw new BadRequestException();
        });
  }

  public void addSamples() {
    var uri = getUri("samples");
    try (var paths = Files.walk(Path.of(uri))) {
      paths.map(captchaFactory::buildEntity)
          .filter(Objects::nonNull)
          .forEach(sampleService::addSample);
    } catch (IOException e) {
      log.error(ERROR_MESSAGE, e);
    }
  }

  @Transactional
  public synchronized String predict(Integer captchaId) {
    try {
      var path = getUri("python").getPath();
      var command = "python3 %s/captcha.py %s %s";
      var process = Runtime.getRuntime().exec(String.format(command, path, path + "/", captchaId));

      var errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      errors.lines().forEach(error -> log.warn("{}", error));

      var output = new BufferedReader(new InputStreamReader(process.getInputStream()));
      var lines = output.lines().collect(Collectors.toList());

      var predictedValue = lines.get(lines.size() - 1);
      log.info("Predicted value: {}", predictedValue);

      if (predictedValue != null) {
        captchaRepository.findById(captchaId).setPredictedValue(predictedValue);
      }
      return predictedValue;
    } catch (Exception e) {
      log.error(ERROR_MESSAGE, e);
      return null;
    }
  }

  private URI getUri(String resourceName) {
    return Optional.ofNullable(getClass().getClassLoader().getResource(resourceName))
        .map(url -> {
          try {
            return url.toURI();
          } catch (URISyntaxException e) {
            throw new NotFoundException();
          }
        })
        .orElseThrow(NotFoundException::new);
  }

}
