# MySQL Container

Une fois la base de données en mémoire désactivée, il faut maintenant créer le container Mysql.

Afin de correspondre aux besoins de l'application, nous allons créer une image Docker dédiée pour les tests.

Dans le répertoire `src/test/resources/mysql` vous trouverez le fichier `Dockerfile`. Ce fichier doit être complété pour ajouter :
 - les variables d'environnement `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`,
 - les ressources `a_schema.sql`, `b_data.sql` dans le répertoire `/docker-entrypoint-initdb.d`.

::: tip
les fichiers de données commencent par une lettre car MySQL va les charger par ordre alphabétique.
:::

Une fois le fichier complété et pour pouvoir l'utiliser directement dans la suite du workshop vous devez construire l'image docker 
à partir de votre Dockerfile en spécifiant un tag, e.g. `mysql:petclinic`.

Vous pouvez également lancer votre image afin de s'assurer qu'elle démarre correctement.

<details>
<summary>Afficher la réponse</summary>

```properties
FROM mysql:5.7.8

ENV MYSQL_ROOT_PASSWORD root_password
ENV MYSQL_DATABASE petclinic
ENV MYSQL_USER petclinic
ENV MYSQL_PASSWORD petclinic

ADD mysql/a_schema.sql /docker-entrypoint-initdb.d
ADD mysql/b_data.sql /docker-entrypoint-initdb.d
```

```bash
docker build -t mysql:petclinic .

docker run -p 3306:3306 mysql:petclinic
```
</details>

<br/>
<br/>

## Intégration dans les tests

Il s'agit maintenant d'utiliser votre image docker pour les tests de la classe `OwnerRepositoryTests`. 

En utilisant les annotations `BeforeClass` et `AfterClass`, vous pouvez lancer et arrêter votre container pour les tests.

Pour fonctionner, votre container doit exposer le port 3306 et doit également indiquer à `Testcontainers` à quel moment le container [est prêt à être utilisé](https://www.testcontainers.org/features/startup_and_waits/).

Vous devez utiliser l'objet `GenericContainer` pour créer votre container en lui indiquant l'image à utiliser.

<details>
<summary>Afficher la réponse</summary>

```java
    private static GenericContainer container;

    @BeforeClass
    public static void setUp() {
        container = new GenericContainer("mysql:petclinic")
            .withExposedPorts(3306)
            .waitingFor(Wait.forListeningPort());

        container.setPortBindings(Lists.newArrayList("3306:3306"));
        container.start();
    }

    @AfterClass
    public static void tearDown() {
        container.stop();
    }
```

</details>


JUnit propose également les annotations `@Rule` et `@ClassRule`. Ces annotations permettent l'injection des `Rule` JUnit 4.

Ce sont des composants qui interceptent les appels aux méthodes de test et qui permettent de réaliser une opération avant et après l'exécution d'une méthode de test.

Remplacer vos précédentes méthodes en utilisant les annotations JUnit.

::: tip
Vous risquez de devoir faire appel à la méthode `withCreateContainerCmdModifier` qui permet de modifier les paramètres de création du container.
:::
 
<details>
<summary>Afficher la réponse</summary>

```java
@ClassRule
public static GenericContainer genericContainer = new GenericContainer("mysql:petclinic")
    .withExposedPorts(3306)
    .waitingFor(Wait.forListeningPort())
    .withCreateContainerCmdModifier(
        new Consumer<CreateContainerCmd>() {
            @Override
            public void accept(CreateContainerCmd createContainerCmd) {
                createContainerCmd.withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306))
                );
            }
        });
```
</details>

<br/>

:::warning

 /!\ Dans le cadre de cet atelier, nous vous montrons comment spécifier et donc figer les ports exposés par le container. Cela peut être utile dans certains cas mais en réalité, il s'agit d'une mauvaise pratique.
 
 Comme expliqué dans [cet article](https://bsideup.github.io/posts/testcontainers_fixed_ports/) d'un des créateurs de Testcontainers, 
 cela peut provoqué des soucis d'allocation de port sur un ordinateur ou une plateforme de CI et cela peut empêcher de lancer les tests en parallèle.
:::

<br/>

### Cycle de vie du container ?

Une fois les tests lancés, en exécutant la commande `docker ps`, que constatez-vous ?

Comment Testcontainers s'assure que le container est correctement démarré ?

Si vous arrêtez vos tests en cours d'exécution, est-ce que votre container est toujours vivant ?

Comment est géré le cycle de vie du container ?

 <details>
<summary>Afficher la réponse</summary>

Lors du lancement d'un container, Testcontainers va également créer un container `quay.io/testcontainers/ryuk`.

Le container [Ryuk](https://github.com/testcontainers/moby-ryuk) est en charge de terminer et de supprimer le container de base de données.
Il se charge également de supprimer les élements associés à un container tel que son volume par exemple.

Dans ce cas précis, c'est grâce aux annotations `@Rule` ou `@ClassRule` qu'est lancé et stoppé le container durant les tests.

</details>

<br/>
<br/>

### Gestion des logs

A ce stade du workshop les tests devraient se lancer correctement tout en utilisant une base de donnée Mysql instanciée dans docker.  
Afin de pouvoir débugger il est souvent utile d'avoir accès aux logs du conteneur docker. Essayez de voir ce que propose Testcontainers
afin de logger la sortie standard (`docker log <conteneurName>`) du conteneur dans un Logger ou tout simplement dans la sortie standard.

<details>
<summary>Afficher la réponse</summary>

```java
// Stream des logs avec un Logger
genericContainer.followOutput(new Slf4jLogConsumer(logger));

// print container log to System.out
genericContainer.withLogConsumer(outputFrame ->
    System.out.println(((OutputFrame)outputFrame).getUtf8String())
);
```
</details>

<br/>
<br/>

### JUnit Jupiter aka JUnit 5

Testcontainers est étroitement couplé avec JUnit4.x car les objets `GenericContainer` sont des rules au sens Junit 4 puisqu'ils implémentent l'interface `org.junit.rules.TestRule`.

Dans le cas où vos tests fonctionnent avec JUnit 5, vous devrez importer la dépendance :

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.12.4</version>
    <scope>test</scope>
</dependency>
```

pour pouvoir utiliser [les extensions Testcontainers](https://www.testcontainers.org/test_framework_integration/junit_5/).

```java
@Testcontainers
class MyTestcontainersTests {

     // will be shared between test methods
    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer();

     // will be started before and stopped after each test method
    @Container
    private PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer()
            .withDatabaseName("foo")
            .withUsername("foo")
            .withPassword("secret");
    @Test
    void test() {
        assertTrue(MY_SQL_CONTAINER.isRunning());
        assertTrue(postgresqlContainer.isRunning());
    }
}
```

<br/>
<br/>

## Singleton Container

En utilisant l'annotation `@ClassRule`, un container est démarré pour chacune des classes de tests. Ce n'est pas vraiment optimal et il est possible de faire en sorte que le container ne soit démarré qu'une seule fois pour l'ensemble de la suite de tests.

Utilisez un mot clef du langage Java pour avoir un singleton de l'objet `GenericContainer` et donc qu'un seul conteneur instancié dans le daemon docker.

Une fois le singleton mis en place, relancez la suite de tests et mesurez le temps d'exécution. Que constatez-vous ?

<details>
<summary>Afficher la réponse</summary>

```java
    private static GenericContainer genericContainer;

    static {
        genericContainer = new GenericContainer("mysql:petclinic")
            .withExposedPorts(3306)
            .waitingFor(Wait.forListeningPort())
            .withCreateContainerCmdModifier(
                createContainerCmd -> ((CreateContainerCmd) createContainerCmd).withPortBindings(
                    new PortBinding(Ports.Binding.bindPort(3306), new ExposedPort(3306))
                ));
        genericContainer.start();
    }
    // genericContainer.close() non utile ici car le container de supervision (Ryuk) s'en occupe
```
</details>

<br/>
<br/>

## JDBC URL Container

Testcontainers propose également d'intégrer un container de base de données [directement depuis le driver](https://www.testcontainers.org/modules/databases/#jdbc-url-examples) de communication avec la base de données. 

Ainsi, le driver `ContainerDatabaseDriver` fourni par Testcontainers se charge de démarrer le container à la première connexion à la base de données. 

De plus, toute la configuration de création du container est déclarée dans l'URL de connexion.

Pour pouvoir utiliser cette façon de déclarer le container MySQL, il faut modifier la propriété de l'URL de connexion à la base :

```properties
spring.datasource.url=jdbc:tc:mysql:petclinic://localhost/petclinic
```

- `jdbc:tc`, indique que l'on utilise Testcontainers comme JDBC provider
- `mysql:petclinic`, ici on déclare notre image Docker
- `localhost`, le nom du host du serveur, il est possible de mettre n'importe quelle valeur
- `petclinic`, le nom de la base de données


Puis, il est nécessaire de déclarer le driver fourni par Testcontainers :

```properties
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
```

Afin d'utiliser ce driver il faut aussi ajouter la dépendance testcontainers correspondant à une base de donnée mysql. 
Dépendance que vous pourrez trouver sur [maven repository](https://mvnrepository.com/artifact/org.testcontainers)

:::tip

À l'heure actuelle, le driver `ContainerDatabaseDriver` ne supporte que les bases de données de type MySQL, PostgreSQL et Oracle. 

:::

<details>
<summary>Afficher la réponse</summary>

```java
@RunWith(SpringRunner.class)
@DataJpaTest(
    properties = {
        "spring.datasource.url=jdbc:tc:mysql:petclinic://localhost/petclinic",
        "spring.datasource.username=petclinic",
        "spring.datasource.password=petclinic",
        "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
    },
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Repository.class)
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractRepositoryTests {}
```

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.12.4</version>
    <scope>test</scope>
</dependency>
```
</details>

L'URL de connexion à la base de données ne contient pas de port, quel est le port réellement exposé par le container ?

<details>
<summary>Afficher la réponse</summary>

Lorsque le port n'est pas fixé par le programme, Testcontainers va choisir un port libre au hasard pour réaliser le binding de ports. Il est possible de récupérer le port mappé une fois le container démarré :

```java
    container.getMappedPort(3306);
```
</details>


## Optimisation

Lors du build de l'application, les tests d'intégrations peuvent prendre un certain temps à se lancer et à s'exécuter. Il est parfois important d'optimiser le temps d'exécution.

### Vérification système

Avant de lancer un container, Testcontainers réalise des vérifications système tel que : la version de Docker utilisée, l'espace disque libre, la disponibilité des ports exposés, etc ...

On peut voir ces contrôles dans les logs de démarrage :

```
INFO 11070 --- [main] org.testcontainers.DockerClientFactory   : Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
        ℹ︎ Checking the system...
        ✔ Docker version should be at least 1.6.0
        ✔ Docker environment should have more than 2GB free disk space
```

Une fois que l'on s'est assuré que l'on peut correctement lancer des containers à l'aide de Testcontainers, il n'est plus nécessaire de réaliser ces contrôles et l'on peut gagner quelques secondes sur le temps de démarrage des tests.

Pour ce faire, éditez le fichier `$HOME/.testcontainers.properties` pour y ajouter 

```
checks.disable=true
```

### Réutilisation des containers

[Un travail est en cours](https://github.com/testcontainers/testcontainers-java/pull/1781) par les contributeurs du projet Testcontainers pour permettre de réutiliser un container mis en place lors d'un test.

Pour pouvoir tester cette nouvelle fonctionnalité, il est nécessaire d'éditer le fichier `$HOME/.testcontainers.properties` en ajoutant :

```
testcontainers.reuse.enable=true
```

Enfin, il faut indiquer au driver `ContainerDatabaseDriver` de réutiliser le container créée avec l'option `TC_REUSABLE` :

```

jdbc:tc:mysql:petclinic://localhost/petclinic?TC_REUSABLE=true

```

Lancez à deux reprises les tests, que constatez-vous ? 

<details>
<summary>Afficher la réponse</summary>

Le container n'est ni stoppé ni supprimé. Il n'y a pour l'instant aucune politique de nettoyage ni de TTL. C'est donc à la charge de l'utilisateur d'effectuer le nettoyage sur son ordinateur ou la plateforme de CI.

</details>

:::warning

Attention, malgré la présence de cette fonctionnalité au sein de la librairie, elle n'est pour le moment qu'au stade "expérimentale" et est à utiliser en connaissance de cause.

:::
