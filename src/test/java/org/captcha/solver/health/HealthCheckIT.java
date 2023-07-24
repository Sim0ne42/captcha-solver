package org.captcha.solver.health;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.ws.rs.core.MediaType;
import org.captcha.solver.CustomTestProfile;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(CustomTestProfile.class)
class HealthCheckIT {

  @Test
  void testLiveness() {
    var status = getStatus("/q/health/live");
    assertEquals(HealthCheckResponse.Status.UP.name(), status);
  }

  @Test
  void testReadiness() {
    var status = getStatus("/q/health/ready");
    assertEquals(HealthCheckResponse.Status.UP.name(), status);
  }

  private String getStatus(String path) {
    return given()
        .when().get(path)
        .then()
        .statusCode(HttpResponseStatus.OK.code())
        .contentType(MediaType.APPLICATION_JSON)
        .extract()
        .body()
        .jsonPath()
        .get("status")
        .toString();
  }

}
