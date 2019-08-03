package org.springframework.samples.petclinic.selenium;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class OwnersPageIHMTest {

    private WebDriver webDriver;

    private static BrowserWebDriverContainer genericContainer;

    private static DockerComposeContainer application;


    static {
        application = new DockerComposeContainer(new File("/home/victor/Application/git/handson-testcontainers/docker-compose.yml"))
            .withExposedService("app", 8080,
                Wait.forHttp("/")
                    .forStatusCode(200));

        application.start();

        genericContainer = new BrowserWebDriverContainer()
            .withCapabilities(new FirefoxOptions());
        genericContainer.start();
    }

    @Before
    public void setUp() {
        webDriver = genericContainer.getWebDriver();
    }

    @Test
    public void should_find_jeff_black_owner() throws InterruptedException {
        webDriver.get("http://172.17.0.1:8080/");

        webDriver.findElement(By.cssSelector("[title*='find owners']")).click();

        WebElement lastname = webDriver.findElement(By.id("lastName"));
        lastname.sendKeys("black");
        lastname.submit();

        // On attends que la page soit correctement chargée
        Thread.sleep(1000);
        assertThat(webDriver.getPageSource()).contains("Jeff Black");
    }
}
