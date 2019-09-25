# Page Pattern (Partie Bonus)

## Présentation du Pattern Page

Le [Page Object Model design pattern](https://martinfowler.com/bliki/PageObject.html) est l'un des patterns les plus répandu dans les tests E2E. Ce pattern ce base sur la définition d'une interface qui représente 
une page de l'application testé. Ce pattern permet de réduire les duplications de code et simplifie la maintenance des tests en imposant une structure logicielle.  

L'interface représentant le concept de page est implémenté différemment pour chaque page du site et fournit des méthodes utilitaires propres à chaque page.  

Ces méthodes utilitaires permettent de valider certaines propriétés de chaque page. De plus, les pages sous forme de classe permettent, via des méthodes, d'instancier les pages voisines 
et de naviguer vers celles-ci.

## Refactoring de la classe OwnersPageIHMTest

Après avoir bien compris le principe du Page Pattern, essayez de réfactorer votre classe de tests `OwnersPageIHMTest` en créant une interface
`Page` et en implémentant chaque page de l'application comme des classes. Par exemple des classes `HomePage`, `OwnersPage`, etc ... .

Dans le cas de notre test, cela pourrait être la structure suivante :

```java
    @Test
    public void should_find_jeff_black_owner() throws InterruptedException {
        // On arrive sur la page d'acceuil de l'application
        homePage.goTo();
    
        // On se rend sur la page Owner
        OwnerPage ownerPage = homePage.goToOwnerPage();
        
        // On recherche un propriétaire
        ownerPage.search("black");

        // On s'assure d'avoir trouver le propriétaire Jeff Black
        ownerPage.foundOwner("Jeff Black");
    }
```

<details>
<summary>Afficher la réponse</summary>

Interface Example :

```java
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

    static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

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
```
</details>
