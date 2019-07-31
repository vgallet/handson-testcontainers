package org.springframework.samples.petclinic.selenium.pagepattern;

import org.openqa.selenium.WebDriver;

public class DetailOwnerPage extends AbstractPage {

    public DetailOwnerPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * test if page contain "Jeff Black" {@link String}
     * @return
     */
    public boolean isDetailBlack() {
        return webDriver.getPageSource().contains("Jeff Black");
    }

}
