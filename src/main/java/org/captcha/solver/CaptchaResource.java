package org.captcha.solver;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.captcha.solver.domain.CaptchaVerification;
import org.captcha.solver.domain.MultipartBody;
import org.captcha.solver.service.CaptchaService;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/captcha")
@RequiredArgsConstructor
@Slf4j
public class CaptchaResource {

  private final CaptchaService captchaService;

  @GET
  @Path("/random")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getRandomCaptcha() {
    var captcha = captchaService.getRandom();
    log.info("got captcha with id: {}", captcha.getId());
    return Response.ok(captcha).build();
  }

  @POST
  @Path("/verify")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response verifyCaptcha(@Valid CaptchaVerification verification) {
    if (captchaService.verify(verification)) {
      log.info("Verification passed");
      return Response.ok().build();
    }
    log.warn("Verification failed");
    return Response.status(Response.Status.FORBIDDEN).build();
  }

  @POST
  @Path("/add")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addCaptcha(@Valid @MultipartForm MultipartBody data) {
    captchaService.add(data.getFile(), data.getFilename());
    log.info("Captcha successfully added: {}", data.getFilename());
    return Response.ok().build();
  }

}
