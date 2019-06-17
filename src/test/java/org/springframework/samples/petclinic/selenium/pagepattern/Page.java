package org.springframework.samples.petclinic.selenium.pagepattern;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * interface which represent a GUI interface on website
 */
public interface Page {

    final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * retrieve {@link WebDriver} for interact with selenium page
     * @return
     */
    WebDriver getWebDriver();

    /**
     * get {@link WebElement} by html selector
     * @param htmlSelector
     * @return
     */
    default WebElement getElementBySelector(String htmlSelector) {
        return this.getWebDriver().findElement(By.cssSelector(htmlSelector));
    }

    /**
     * get {@link WebElement} by html id
     * @param idName
     * @return
     */
    default WebElement getElementById(String idName) {
        return this.getWebDriver().findElement(By.id(idName));
    }

    /**
     * get {@link WebElement} by html class
     * @param className
     * @return
     */
    default WebElement getElementClass(String className) {
        return this.getWebDriver().findElement(By.className(className));
    }

    /**
     * generate click event on {@link WebElement}
     * @param element
     */
    default void clickElement(WebElement element) {
        element.click();
    }

    /**
     * take screen shot of current webPage
     * @param outputPath
     * @return
     */
    default File takeScreenshot(String outputPath) {
        File outputFile = ((RemoteWebDriver)getWebDriver()).getScreenshotAs(OutputType.FILE);
        File copied = new File(outputPath);
        try {
            Files.copy(outputFile.toPath(), copied.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.info("Failed to screenshot current page");
            return null;
        }
        return copied;
    }
}
