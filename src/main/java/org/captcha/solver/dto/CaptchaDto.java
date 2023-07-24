package org.captcha.solver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaDto {

  private int id;
  private String base64Image;
  private String format;

}
