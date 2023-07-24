package org.captcha.solver;

import org.captcha.solver.entity.CaptchaEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class TestData {

  public static final byte[] IMAGE = new byte[]{};
  public static final int ID = 1;
  public static final int WIDTH = 200;
  public static final int HEIGHT = 50;
  public static final boolean SAMPLE = true;
  public static final String LABEL = "2b827";
  public static final String FILENAME = "2b827.png";
  public static final String FORMAT = "png";

  public static CaptchaEntity buildEntity() {
    var entity = new CaptchaEntity();
    entity.setImage(IMAGE);
    entity.setLabel(LABEL);
    entity.setPredictedValue(LABEL);
    entity.setFilename(FILENAME);
    entity.setFormat(FORMAT);
    entity.setWidth(WIDTH);
    entity.setHeight(HEIGHT);
    entity.setSample(SAMPLE);
    return entity;
  }

  public static URI getSampleUri() {
    return Optional.ofNullable(TestData.class.getClassLoader().getResource("samples/" + FILENAME))
        .map(url -> {
          try {
            return url.toURI();
          } catch (URISyntaxException e) {
            return null;
          }
        })
        .orElse(null);
  }

}
