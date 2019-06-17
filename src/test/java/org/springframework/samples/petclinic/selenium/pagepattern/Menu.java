package org.springframework.samples.petclinic.selenium.pagepattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Menu {

    private final WebDriver webDriver;
    private final String selector;

    public Menu(WebDriver webDriver, String selector) {
        this.webDriver = webDriver;
        this.selector = selector;
    }

    public boolean goTo(String menuSelectorValue) {
        By menuEntrySelector = By.cssSelector("[" + selector + "*='" + menuSelectorValue + "']");

        try {
            WebElement menuEntry = webDriver.findElement(menuEntrySelector);
            menuEntry.click();
            return true;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }
}
