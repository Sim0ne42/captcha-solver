package org.captcha.solver.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CaptchaVerification {

  @NotNull(message = "captcha id cannot be null")
  @Positive(message = "captcha id must be greater than 0")
  private Integer captchaId;

  @NotBlank(message = "text cannot be blank")
  private String text;

}
