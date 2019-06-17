package org.springframework.samples.petclinic.selenium.pagepattern;

import org.openqa.selenium.WebDriver;

public abstract class AbstractPage implements Page {

    protected final WebDriver webDriver;

    public AbstractPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }
}
