# MySQL Container

Une fois la base de données en mémoire désactivée, il faut maintenant créer le container mysql.

Afin de correspondre au besoin de l'application, nous allons créer une image dédiée pour les tests.

Dans le répertoire `src/test/resources/mysql` vous trouverez le fichier `Dockerfile`. Ce fichier doit être compléter pour ajouter :
 - les variables d'environnement `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`,
 - les ressources `a_schema.sql`, `b_data.sql` dans le répertoire `/docker-entrypoint-initdb.d`.

::: tip
les fichiers de données commencent par une lettre car MySQL va les charger par ordre alphabétique.
:::

Une fois le fichier complété et pour pouvoir l'utiliser directement dans la suite du workshop vous devez construire l'image docker 
à partir de votre Dockerfile en spécifiant un tag, e.g. `mysql-petclinic`.

Vous pouvez également lancer votre image afin de s'assurer qu'elle démarre correctement.

<details>
<summary>Afficher la réponse</summary>

```properties
FROM mysql:5.7.8

ENV MYSQL_ROOT_PASSWORD root_password
ENV MYSQL_DATABASE petclinic
ENV MYSQL_USER petclinic
ENV MYSQL_PASSWORD petclinic

ADD a_schema.sql /docker-entrypoint-initdb.d
ADD b_data.sql /docker-entrypoint-initdb.d
```
</details>

## Intégration dans les tests

Il s'agit maintenant d'utiliser votre image docker pour les tests de la classe `OwnerRepositoryTests`. 

En utilisant les annotations JUnit `@Before` et `@After`, vous pouvez créer votre container avec l'objet `GenericContainer` dans la super classe `AbstractRepositoryTests`
et ainsi avoir une base de données mysql initialisée pour les tests.

Pour fonctionner, votre container doit exposer le port 3306 et doit également indiquer à `Testcontainers` à quel moment le container est prêt à être utilisé.


Une fois les tests lancés, en exécutant la commande `docker ps`, que constatez-vous ?

Comment Testcontainers s'assure que le container est correctement démarré ?

Si vous arrêtez vos tests en cours d'exécution, est-ce que votre container est toujours vivant ?
 
<details>
<summary>Afficher la réponse</summary>
 
```java
private static GenericContainer genericContainer;

@Before
public static void setUp() {
    genericContainer = new GenericContainer("mysql-petclinic");
    genericContainer.setExposedPorts(Lists.newArrayList(3306));
    genericContainer.setPortBindings(Lists.newArrayList("3306:3306"));
    genericContainer.waitingFor(Wait.forListeningPort());

    genericContainer.start();
}

@After
public static void tearDown() throws Exception {
    if(genericContainer != null) {
        genericContainer.stop();
    }
}
``` 
</details>


### Bonus : Gestion des logs

A ce stade du workshop les tests devraient ce lancer correctement tout en utilisant une base de donnée mysql instancié dans docker.  
Afin de pouvoir débugger il est souvent utile d'avoir accés au log du conteneur docker. Essayez de rajouter un `Consumer<OutputFrame>` à l'object `GenericContainer` 
afin de logger la sortie standard (`docker log <conteneurName>`) du conteneur dans la variable `LOGGER` de la classe de test.

<details>
<summary>Afficher la réponse</summary>

```java
// print container log to LOGGER
genericContainer.withLogConsumer(outputFrame ->
    LOGGER.debug(((OutputFrame)outputFrame).getUtf8String())
);
```
</details>

## Cycle de vie automatique

En implémentant des méthodes `static` pour les annotations JUnit `@Before` et `@After`, vous contrôler manuellement le cycle de vie de votre container.

Cela peut être utile et intéressant dans certains cas de figures, mais dans notre situation nous pouvons laisser JUnit gérer le cycle de vie des containers.

Pour cela, JUnit propose les annotations `@Rule` et `@ClassRule`. Vous pouvez ainsi supprimer vos méthodes `@Before` et `@After`pour utiliser une des annotations JUnit.

::: tip
Testcontainers est étroitement couplé avec JUnit4.x. Dans le cas où vos tests fonctionnent avec JUnit 5, vous devrez importer la dépendance

``````xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.11.2</version>
    <scope>test</scope>
</dependency>
```

pour pouvoir utiliser les extensions Testcontainers.
:::

::: tip
Vous risquez de devoir faire appel à la méthode `withCreateContainerCmdModifier` qui permet de modifier les paramètres de création du container.
:::

<details>
<summary>Afficher la réponse</summary>

```java
@Rule
public GenericContainer genericContainer = new GenericContainer("mysql-petclinic")
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

// -------------------------------- or -------------------------------- //

@ClassRule
public static GenericContainer genericContainer = new GenericContainer("mysql-petclinic")
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

## Singleton Container

En utilisant l'annotation `@ClassRule`, un container est démarré pour chacune des classes de tests. Ce n'est pas vraiment optimal et il est possible de faire en sorte que le container ne soit démarré qu'une fois pour l'ensemble de la suite de tests.

Utilisez un mot clef du langage java pour avoir un singleton de l'objet `GenericContainer` et donc qu'un seul conteneur instancié dans le daemon docker.

Une fois le singleton mis en place, relancez la suite de tests et mesurez le temps d'exécution. Que constatez-vous ?

