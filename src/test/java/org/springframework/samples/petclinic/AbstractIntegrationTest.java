package org.springframework.samples.petclinic;

import org.junit.AfterClass;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;

public class AbstractIntegrationTest {

    protected static BrowserWebDriverContainer genericContainer;

    static {
        genericContainer = new BrowserWebDriverContainer().withCapabilities(new FirefoxOptions());
        genericContainer.start();
    }

    // clean container
    @AfterClass
    public static void tearDown() {
        if (genericContainer != null) {
            genericContainer.stop();
        }
    }

}
