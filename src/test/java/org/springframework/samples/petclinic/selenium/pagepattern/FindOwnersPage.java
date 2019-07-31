package org.springframework.samples.petclinic.selenium.pagepattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FindOwnersPage extends AbstractPage {

    public FindOwnersPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * access html lastName input text
     * @return
     */
    public <T extends Page> T searchOwner(String ownerName) throws InterruptedException {
        WebElement lastname = this.getElementById("lastName");

        lastname.sendKeys(ownerName);
        lastname.submit();

        // On attends que la page soit correctement chargée
        Thread.sleep(1000);

        boolean isDetailPage = webDriver.getCurrentUrl().matches(".*/owners/[\\d]");
        boolean isSearchRequest = webDriver.getCurrentUrl().matches(".*/owners\\?lastName=.*");
        if(isDetailPage || isSearchRequest) {
            if(isNotFound()) {
                return (T) new NotFoundOwnerPage(webDriver);
            }
            return (T) new DetailOwnerPage(webDriver);
        }
        else {
            return (T) this;
        }
    }

    /**
     * test if page is not found result page
     * @return
     */
    public boolean isNotFound() {
        return webDriver.getPageSource().contains("has not been found");
    }

}
