package org.captcha.solver;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.captcha.solver.service.CaptchaService;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@Slf4j
class SeleniumIT {

  @Inject
  CaptchaService captchaService;

  @Test
  void solveCaptcha() {
    var driver = new ChromeDriver();
    try {
      assertDoesNotThrow(() -> predictValueAndSubmit(driver));
    } catch (Exception e) {
      fail();
    } finally {
      driver.quit();
    }
  }

  @SneakyThrows
  private void predictValueAndSubmit(RemoteWebDriver driver) {
    // Load page
    var index = getClass().getClassLoader().getResource("index.html");
    driver.get(String.format("file:///%s", index.toURI().getPath()));
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

    // Check if captcha is present
    var image = driver.findElement(By.className("captcha_image"));
    assertNotEquals("images/loading.gif", image.getAttribute("src"));

    // Predict captcha
    var id = String.valueOf(driver.executeScript("return captchaId"));
    var predictedValue = assertDoesNotThrow(() -> captchaService.predict(Integer.valueOf(id)));
    assertNotNull(predictedValue);

    // Submit text
    var submitButton = driver.findElement(By.className("submit_button"));
    var textBox = driver.findElement(By.id("captcha_text"));
    addText(textBox, predictedValue);
    randomDelay();
    submitButton.click();
    Thread.sleep(4000);

    // Check if entered text is correct
    var message = driver.findElement(By.className("message"));
    assertEquals("rgba(37, 205, 37, 1)", message.getCssValue("color"));
    assertEquals("Entered text is correct", message.getText());
  }

  @SneakyThrows
  private void addText(WebElement textBox, String predictedValue) {
    for (char c : predictedValue.toCharArray()) {
      randomDelay();
      textBox.sendKeys(String.valueOf(c));
    }
  }

  @SneakyThrows
  private void randomDelay() {
    Thread.sleep(new Random().nextInt(500) + 500);
  }

}
