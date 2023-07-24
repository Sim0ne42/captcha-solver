package org.captcha.solver;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class CustomTestProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
        "quarkus.datasource.db-kind", "h2",
        "quarkus.datasource.username", "",
        "quarkus.datasource.password", "",
        "quarkus.datasource.jdbc.url", "jdbc:h2:mem:default"
    );
  }

}
