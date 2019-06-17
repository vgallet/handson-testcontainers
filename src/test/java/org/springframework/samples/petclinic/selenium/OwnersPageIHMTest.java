package org.springframework.samples.petclinic.selenium;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.samples.petclinic.AbstractIntegrationTest;
import org.springframework.samples.petclinic.UtilsTest;
import org.springframework.samples.petclinic.selenium.pagepattern.*;

import java.io.File;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class OwnersPageIHMTest extends AbstractIntegrationTest {

    private WebDriver webDriver;
    private static String dockerIpv4 = UtilsTest.getDockerInterfaceIp(Pattern.compile("docker[\\d]"));
    private String serverUrl;

    @Before
    public void setUp() {
        webDriver = genericContainer.getWebDriver();
        serverUrl = "http://" + dockerIpv4 + ":8080/";
    }

    @Test
    public void should_find_jeff_black_owner() throws InterruptedException {
        HomePage home = new HomePage(webDriver, serverUrl);
        Page changeToFindOwnerSuccess = home.goToFindOwners();

        // stay on home if go to find owners page failed
        assertThat(changeToFindOwnerSuccess).isNotEqualTo(home);

        Page detailBlack = ((FindOwnersPage)changeToFindOwnerSuccess).searchOwner("black");

        // stay on FindOwnersPage if search owner failed
        assertThat(detailBlack).isNotEqualTo(changeToFindOwnerSuccess);

        // test if search sucess
        assertThat(detailBlack).isNotInstanceOf(NotFoundOwnerPage.class);

        // test if search return Jeff Black DetailOwnerPage
        assertThat(((DetailOwnerPage)detailBlack).isDetailBlack()).isTrue();
    }

    @Test
    public void should_not_find_toto_owner() throws InterruptedException {
        HomePage home = new HomePage(webDriver, serverUrl);
        Page changeToFindOwnerSuccess = home.goToFindOwners();

        // stay on home if go to find owners page failed
        assertThat(changeToFindOwnerSuccess).isNotEqualTo(home);

        Page detailBlack = ((FindOwnersPage)changeToFindOwnerSuccess).searchOwner("toto");

        // stay on FindOwnersPage if search owner failed
        assertThat(detailBlack).isNotEqualTo(changeToFindOwnerSuccess);

        // test if search sucess
        assertThat(detailBlack).isInstanceOf(NotFoundOwnerPage.class);
    }

    @Test
    public void take_screenshot_jeff_black_owner() throws InterruptedException {
        HomePage home = new HomePage(webDriver, serverUrl);
        Page changeToFindOwnerSuccess = home.goToFindOwners();
        assertThat(changeToFindOwnerSuccess).isInstanceOf(FindOwnersPage.class);
        Page detailBlack = ((FindOwnersPage)changeToFindOwnerSuccess).searchOwner("black");

        // take screenshot and assert that it is not null
        File detailBlackScreenshot = detailBlack.takeScreenshot("./screenshot.png");
        assertThat(detailBlackScreenshot).isNotNull();
    }
}
