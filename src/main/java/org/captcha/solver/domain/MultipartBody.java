package org.captcha.solver.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

@Data
public class MultipartBody {

  @FormParam("file")
  @PartType(MediaType.APPLICATION_OCTET_STREAM)
  @NotNull(message = "file cannot be null")
  private InputStream file;

  @FormParam("filename")
  @PartType(MediaType.TEXT_PLAIN)
  @NotBlank(message = "filename cannot be blank")
  private String filename;

}
