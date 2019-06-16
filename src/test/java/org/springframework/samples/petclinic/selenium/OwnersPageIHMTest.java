package org.springframework.samples.petclinic.selenium;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.samples.petclinic.AbstractIntegrationTest;
import org.springframework.samples.petclinic.UtilsTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class OwnersPageIHMTest extends AbstractIntegrationTest {

    private WebDriver webDriver;
    private static String dockerIpv4 = UtilsTest.getDockerInterfaceIp(Pattern.compile("docker[\\d]"));

    @Before
    public void setUp() {
        webDriver = genericContainer.getWebDriver();
    }

    @Test
    public void should_find_jeff_black_owner() throws InterruptedException {
        webDriver.get("http://" + dockerIpv4 + ":8080/");

        webDriver.findElement(By.cssSelector("[title*='find owners']")).click();

        WebElement lastname = webDriver.findElement(By.id("lastName"));
        lastname.sendKeys("black");
        lastname.submit();

        // On attends que la page soit correctement chargée
        Thread.sleep(1000);
        assertThat(webDriver.getPageSource()).contains("Jeff Black");
    }

    @Test
    public void take_screenshot_jeff_black_owner() throws InterruptedException, IOException {
        webDriver.get("http://" + dockerIpv4 + ":8080/");

        webDriver.findElement(By.cssSelector("[title*='find owners']")).click();
        WebElement lastname = webDriver.findElement(By.id("lastName"));
        lastname.sendKeys("black");
        lastname.submit();
        // On attends que la page soit correctement chargée
        Thread.sleep(1000);

        File outputFile = ((RemoteWebDriver)webDriver).getScreenshotAs(OutputType.FILE);
        File copied = new File("./screenshot.png");
        Files.copy(outputFile.toPath(), copied.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
