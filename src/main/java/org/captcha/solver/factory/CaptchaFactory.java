package org.captcha.solver.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.captcha.solver.entity.CaptchaEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static org.captcha.solver.exception.ExceptionHandler.ERROR_MESSAGE;

@ApplicationScoped
@Slf4j
public class CaptchaFactory {

  private final Set<String> supportedFormats;

  public CaptchaFactory(
      @ConfigProperty(name = "supported.formats", defaultValue = "png,jpg") Set<String> supportedFormats) {
    this.supportedFormats = supportedFormats;
  }

  public CaptchaEntity buildEntity(Path path) {
    try {
      var file = path.toFile();
      var bytes = Files.readAllBytes(path);
      return Optional.ofNullable(ImageIO.read(file))
          .map(image -> buildEntity(image, bytes, file.getName(), true))
          .orElse(null);
    } catch (IOException e) {
      log.error(ERROR_MESSAGE, e);
    }
    return null;
  }

  public CaptchaEntity buildEntity(InputStream inputStream, String filename) {
    try {
      var bytes = inputStream.readAllBytes();
      return Optional.ofNullable(ImageIO.read(new ByteArrayInputStream(bytes)))
          .map(image -> buildEntity(image, bytes, filename, false))
          .orElse(null);
    } catch (IOException e) {
      log.error(ERROR_MESSAGE, e);
    }
    return null;
  }

  private CaptchaEntity buildEntity(BufferedImage image, byte[] bytes, String filename, boolean isSample) {
    var split = filename.split("\\.");
    if (split.length == 2) {
      var label = split[0];
      var formatName = split[1];
      if (supportedFormats.contains(formatName)) {
        var entity = new CaptchaEntity();
        entity.setImage(Base64.getEncoder().encode(bytes));
        entity.setLabel(label);
        entity.setFilename(filename);
        entity.setFormat(formatName);
        entity.setWidth(image.getWidth());
        entity.setHeight(image.getHeight());
        entity.setSample(isSample);
        return entity;
      } else {
        var message = "Unsupported format";
        log.warn(message + ": {}", formatName);
        throw new BadRequestException(message);
      }
    } else {
      var message = "Incorrect filename";
      log.warn(message + ": {}", filename);
      throw new BadRequestException(message);
    }
  }

}
