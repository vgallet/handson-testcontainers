package org.springframework.samples.petclinic.selenium;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BrowserWebDriverContainer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PetClinicApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OwnersPageIHMTest {

    private static BrowserWebDriverContainer genericContainer;

    static {
        DesiredCapabilities firefox = DesiredCapabilities.firefox();
        firefox.setPlatform(Platform.LINUX);
        firefox.setVersion("66");

        genericContainer = new BrowserWebDriverContainer()
            .withCapabilities(firefox);
        genericContainer.start();
    }

    private WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = genericContainer.getWebDriver();
    }

    @Test
    public void should_find_jeff_black_owner() throws InterruptedException {
        webDriver.get("http://172.18.0.1:8080/");

        webDriver.findElement(By.cssSelector("[title*='find owners']")).click();

        WebElement lastname = webDriver.findElement(By.id("lastName"));
        lastname.sendKeys("black");
        lastname.submit();

        // On attends que la page soit correctement chargée
        Thread.sleep(1000);
        assertThat(webDriver.getPageSource()).contains("Jeff Black");
    }
}
