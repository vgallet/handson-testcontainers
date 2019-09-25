# Web Browser Container


## Les Tests d'acception E2E

Les tests d'acceptation sont une phase d'un processus de développement logiciel. Ils permettent de déterminer si le produit a satisfait les spécifications globales des exigences et s'il est «accepté» comme prêt à être livré.

Il s'agit généralement de la dernière phase de test avant la mise en production du produit.

Ils existent plusieurs solutions pour mettre en place des tests d'IHM, [SeleniumHQ](https://www.seleniumhq.org/), [Cypress](https://www.cypress.io/), [PhantomJS](http://phantomjs.org/), etc ... 

Il n'est jamais simple de maintenir une base de tests fonctionnels, de tests E2E. Ces derniers sont par nature très volatiles puisqu'ils couvrent de grands pans de l'application. 

De plus, cela nécessite beaucoup de configurations, ce qui augmente les chances d'échecs lorsque les tests sont exécutés sur différentes machines ou dans un environnement CI (intégration continue).

L'installation et la maintenance de différents navigateurs et WebDrivers pour les tests locaux et les tests de CI prennent du temps.

Et même une fois terminé, ils peuvent toujours échouer à cause de problèmes simples. Par exemple si la résolution d'écran sur les machines locales de développement et dans une CI sont différentes.


## Tests E2E explication et lancement initial

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
Avant de démarrer la migration vers Testcontainers, assurez-vous que le test `OwnersPageIHMTest.java` fonctionne.
:::

## Lancement de tests selenium avec un navigateur conteneurisé

Pour la suite, il est nécéssaire d'importer la dépendance suivante :

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>selenium</artifactId>
    <version>1.11.2</version>
    <scope>test</scope>
</dependency>
```

Une fois le module importé, vous pouvez créer votre container docker exposant un navigateur (Firefox par defaut) grâce à la classe fournie par Testcontainers : `BrowserWebDriverContainer`.

Le container précédemment crée vous permet de remplacer le driver `HtmlUnitDriver` par un driver Firefox. 

Sans modification particulière de la méthode `should_find_jeff_black_owner()` celle-ci devrait échouer, trouver pourquoi et remédier à ce problème.

::: tip
Notez que le serveur web écoute sur l'interface réseau `0.0.0.0/8080`, alors le navigateur firefox lancé se trouve au sein d'un conteneur.
:::

<details>
<summary>Afficher la réponse</summary>

```java
private static BrowserWebDriverContainer genericContainer;

static {
    genericContainer = new BrowserWebDriverContainer()
        .withCapabilities(new FirefoxOptions());
    genericContainer.start();
}
    
private WebDriver webDriver;

@Before
public void setUp() {
    webDriver = genericContainer.getWebDriver();
}

@Test
public void should_find_jeff_black_owner() throws InterruptedException {
    // L'adresse IP peut être obtenue sous linux avec à partir de l'interface réseau docker0
    // Sous Mac et Windows, à partir de la version 18.03 de Docker, il est possible d'utiliser `host.docker.internal` 
    webDriver.get("http://" + dockerIp + ":8080/");
    webDriver.findElement(By.cssSelector("[title*='find owners']")).click();
    
    ...
}
```
</details>

## Screenshots

Dans cette partie vous allez devoir réaliser une capture d'écran du navigateur lancé dans le conteneur et persister l'image sur votre disque.  
En reprenant le test fournis (`OwnersPageIHMTest`), capturer le resultat de la recherche du propriétaire "Jeff Black".

::: tip
Si vous souhaitez modifier la résolution de la capture d'écran, se conférer aux variables d'environnement disponible pour l'image docker `selenium/standalone-firefox-debug`.  
:::

<details>
<summary>Afficher la réponse</summary>

```java
// into AbstractIntegrationTest.java
Map<String, String> envs = new HashMap<>();
envs.put("SCREEN_WIDTH", "1366");
envs.put("SCREEN_HEIGHT", "768");
envs.put("SCREEN_DEPTH", "24");

genericContainer = (BrowserWebDriverContainer) new BrowserWebDriverContainer()
    .withCapabilities(new FirefoxOptions())
    .withEnv(envs);
genericContainer.start();


// into OwnersPageIHMTest.java
@Test
public void take_screenshot_jeff_black_owner() throws InterruptedException, IOException {
    webDriver.get("http://" + dockerIpv4 + ":8080/");

    webDriver.findElement(By.cssSelector("[title*='find owners']")).click();
    WebElement lastname = webDriver.findElement(By.id("lastName"));
    lastname.sendKeys("black");
    lastname.submit();
    // On attends que la page soit correctement chargée
    Thread.sleep(1000);

    File outputFile = ((RemoteWebDriver)webDriver).getScreenshotAs(OutputType.FILE);
    File copied = new File("./screenshot.png");
    Files.copy(outputFile.toPath(), copied.toPath(), StandardCopyOption.REPLACE_EXISTING);
}
```
</details>

## Différents navigateurs

Nos tests fonctionnent maintenant avec un navigateur Firefox mais de quelle version s'agit-il ? 

En utilisant la classe `DesiredCapabilities` fournit par Selenium, spécifiez la version de Firefox avec une version `66` par exemple.

<details>
<summary>Afficher la réponse</summary>

```java
static {
    DesiredCapabilities firefox = DesiredCapabilities.firefox();
    firefox.setPlatform(Platform.LINUX);
    firefox.setVersion("66");

    genericContainer = new BrowserWebDriverContainer()
        .withCapabilities(firefox);
    genericContainer.start();
}
```
</details>

Lancez le test et contrôlez alors la version de Firefox utilisé, que constatez-vous ?

:::tip
Vous pouvez connaître la version de Firefox en vous connectant directement au conteneur.

<details>
<summary>Affichez la réponse</summary>

```sh
docker exec -it <containerId> firefox -v
```
</details>
</br>
:::

En réalité, le module Selenium fournit par Testcontainers se base sur le projet [docker-selenium](https://github.com/SeleniumHQ/docker-selenium). Ce dernier ne propose à l'heure actuelle que les navigateurs internet Firefox et Chrome.

Ainsi, il est seulement possible pour l'instant de n'exécuter ses tests IHM avec Chrome 74 ou Firefox 67.

<details>
<summary>Afficher la réponse</summary>

```java
static {
    DesiredCapabilities chrome = DesiredCapabilities.chrome();

    genericContainer = new BrowserWebDriverContainer()
        .withCapabilities(chrome);
    genericContainer.start();
}
```
</details>


En réalité, pour pouvoir proposer ces deux navigateurs le conteneur `BrowserWebDriverContainer` met en place le mécanisme de Selenium Grid. Grâce à ce mécanisme, on peut s'attendre à ce que la liste des navigateurs et de leurs versions s'étoffent dans le futur.

### Selenium Grid

Selenium Grid est un outil qui permet d’exécuter les tests sur différentes machines avec différents navigateurs. Il fait partie de la suite Selenium, spécialisé dans l'exécution de plusieurs tests sur différents navigateurs, systèmes d'exploitation et machines.

Selenium Grid comprend deux éléments principaux: **Hub** et **Nodes**.

![Selenium Grid](selenium_grid.png)

#### Hub

Le Hub est le point central dans lequel nous pouvons charger nos tests. Il agit également en tant que serveur car il contrôle le réseau de machines de test. 

On ne compte qu'un seul Hub, maître du réseau, dans une Selenium Grid.

Lorsqu'un test se lance avec des `DesiredCapabilities`, le Hub recherche le noeud qui correspond à la configuration donnée.

Par exemple, vous pouvez indiquer que vous souhaitez exécuter le test sous Windows 10 et sur le navigateur Chrome avec version XXX. Le Hub tentera de trouver une machine dans son réseau qui correspond aux critères et exécutera le test sur cette dernière. 

S'il n'y a pas de correspondance, le Hub renverra une erreur.

#### Node

Un Node est simplement une machine qui s'est enregistrée auprès du Hub pour lui spécifier ses capacités. Par exemple, il s'agit d'une machine sous Linux, disposant d'un navigateur Firefox XXX et Chrome XXX.


