package org.springframework.samples.petclinic.selenium.pagepattern;

import org.openqa.selenium.WebDriver;
import org.springframework.samples.petclinic.UtilsTest;

import java.util.regex.Pattern;

public class HomePage extends AbstractPage {

    private final Menu menu;

    public HomePage(WebDriver webDriver, String url) {
        super(webDriver);
        webDriver.get(url);
        menu = new Menu(webDriver, "title");
    }

    public <T extends Page> T goToFindOwners() {
        boolean success = menu.goTo("find owners");

        if(success) {
            return (T) new FindOwnersPage(webDriver);
        }
        else {
            return (T) this;
        }
    }
}
