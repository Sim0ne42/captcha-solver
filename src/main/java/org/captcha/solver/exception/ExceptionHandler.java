package org.captcha.solver.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class ExceptionHandler implements ExceptionMapper<Exception> {

  public static final String ERROR_MESSAGE = "Something went wrong";

  @Override
  public Response toResponse(Exception e) {
    if (e instanceof BadRequestException) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(e.getMessage())
          .build();
    }

    if (e instanceof NotFoundException) {
      return Response.status(Response.Status.NOT_FOUND)
          .entity(e.getMessage())
          .build();
    }

    if (e instanceof NotAllowedException) {
      return Response.status(Response.Status.METHOD_NOT_ALLOWED)
          .entity(e.getMessage())
          .build();
    }

    log.error(ERROR_MESSAGE, e);
    return Response.serverError().build();
  }

}
