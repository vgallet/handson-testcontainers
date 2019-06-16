package org.springframework.samples.petclinic;

import org.junit.AfterClass;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.util.HashMap;
import java.util.Map;

public class AbstractIntegrationTest {

    protected static BrowserWebDriverContainer genericContainer;

    static {
        Map<String, String> envs = new HashMap<>();
        envs.put("SCREEN_WIDTH", "1366");
        envs.put("SCREEN_HEIGHT", "768");
        envs.put("SCREEN_DEPTH", "24");

        genericContainer = (BrowserWebDriverContainer) new BrowserWebDriverContainer()
            .withCapabilities(new FirefoxOptions())
            .withEnv(envs);

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
