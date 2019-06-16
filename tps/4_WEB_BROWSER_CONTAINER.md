# Web Browser Container


## Les Tests d'acception E2E

Les tests d'acceptation sont une phase d'un processus de développement logiciel. Ils permettent de déterminer si le produit a satisfait les spécifications globales des exigences et s'il est «accepté» comme prêt à être livré.

Il s'agit généralement de la dernière phase de test avant la mise en production du produit.

Ils existent plusieurs solutions pour mettre en place des tests d'IHM, [SeleniumHQ](https://www.seleniumhq.org/), [Cypress](https://www.cypress.io/), [PhantomJS](http://phantomjs.org/), etc ... 

Il n'est jamais simple de maintenir une base de tests fonctionnels, de tests E2E. Ces derniers sont par nature très volatiles puisqu'ils couvrent de grands pans de l'application. 

De plus, cela nécessite beaucoup de configuration qui peut encore facilement échouer lorsqu’il est exécuté sur différentes machines ou dans un environnement CI (intégration continue).

L'installation et la maintenance de différents navigateurs et WebDrivers pour les tests locaux et les tests de CI prennent du temps.

Et même une fois terminé, il peut toujours échouer à cause de problèmes simples. Par exemple si la résolution d'écran sur les machines locales de développement et dans une CI sont différentes.


## lancement de tests selenium avec un navigateur conteneurisé

Dans le code fournis, la classe `OwnersPageIHMTest.java` est un test se basant sur l'outil Selenium et plus précisement sur le driver [HtmlUnitDriver](https://github.com/SeleniumHQ/htmlunit-driver) 
qui est une abstraction pour manipuler le navigateur headless [HtmlUnit](http://htmlunit.sourceforge.net/).

```java
    @Before
    public void setUp() {
        webDriver = new HtmlUnitDriver();
    }
```

Les drivers selenium permettent d'avoir une abstraction entre le navigateur internet que l'on manipule et les actions que l'on réalise.

On trouve notamment les classes `FirefoxDriver`, `ChromeDriver`, `EdgeDriver`, `InternetExplorerDriver`, etc ...  
Ces classes sont des implémentations différentes de la classe `WebDriver`. Ces implémentations sont propres au navigateur avec lequel on souhaite communiquer.

Grâce à Testcontainers, il est possible d'encapsuler le navigateur dans un conteneur et de maintenir facilement la version utilisée.


::: tip
Avant de démarrer la migration vers test containers, assurez-vous que le test `OwnersPageIHMTest.java` fonctionne. Il s'agit d'un test bout-en-bout qui nécessite donc que l'application soit démarrée ;-)
:::

Pour la suite, il est nécéssaire d'importer la dépendance suivante :

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>selenium</artifactId>
    <version>1.11.2</version>
    <scope>test</scope>
</dependency>
```

Une fois le module importé, vous pouvez créer votre container docker exposant un navigateur Firefox grâce à la classe fournie par Testcontainers : `BrowserWebDriverContainer`.
Pour cela il est conseillez de reprendre exemple sur la classe `AbstractRepositoryTests` de la partie précédente.

Le container précédemment crée vous permet de remplacer le driver `HtmlUnitDriver` par un driver Firefox. 

<details>
<summary>Afficher la réponse</summary>

```java
    private static BrowserWebDriverContainer genericContainer;
    private WebDriver webDriver;
    
    static {
        genericContainer = new BrowserWebDriverContainer()
            .withCapabilities(new FirefoxOptions());
        genericContainer.start();
    }

    @Before
    public void setUp() {
        webDriver = genericContainer.getWebDriver();
    }
    
    @Test
    public void should_find_jeff_black_owner() throws InterruptedException {
        webDriver.get("http://localhost:8080/");
        webDriver.findElement(By.cssSelector("[title*='find owners']")).click();
        
        ...
    }

    // clean container
    @AfterClass
    public static void tearDown() {
        if (genericContainer != null) {
            genericContainer.stop();
        }
    }
```
</details>

## Screenshots
// TODO

## Différents navigateurs
// TODO

### Selenium Grid

L'utilisation de la selenium grid

![Selenium Grid](selenium_grid.png)

