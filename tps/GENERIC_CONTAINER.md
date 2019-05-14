# MySQL Container

Une fois la base de données en mémoire désactivée, il faut maintenant créer le container mysql.

Afin de correspondre au besoin de l'application, nous allons créer une image dédiée pour les tests.

Dans le répertoire `src/test/resources/mysql` vous trouverez le fichier `Dockerfile`. Ce fichier doit être compléter pour ajouter :
 - les variables d'environnement `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`,
 - les ressources `a_schema.sql`, `b_data.sql` dans le répertoire `/docker-entrypoint-initdb.d`.

::: tip
les fichiers de données commencent par une lettre car MySQL va les charger par ordre alphabétique.
:::

Une fois le fichier complété, vous pouvez construire l'image à partir de votre Dockerfile en spécifiant le tag `mysql-petclinic`.

Vous pouvez également lancer votre image afin de s'assurer qu'elle démarre correctement.

## Intégration dans les tests

Il s'agit maintenant d'utiliser votre image docker pour les tests de la classe `OwnerRepositoryTests`. 

En utilisant les annotations JUnit `@BeforeClass` et `AfterClass`, vous pouvez créer votre container avec la classe `GenericContainer` et ainsi avoir une base de données mysql initialisée pour les tests.

Pour fonctionner, votre container doit donc exposer le port 3306 et il faut également indiquer à `testcontainers` à quel momenet le container est prêt à être utilisé.


Une fois les tests lancés, en exécutant la commande `docker ps`, que constatez-vous ?

 

## Cycle de vie automatique

En implémentant des méthodes pour les annotations JUnit `@BeforeClass` et `AfterClass`, vous contrôler manuellement le cycle de vie de votre container. 

Cela peut être utile et intéressant dans certains cas de figures, mais dans notre situtation nous pouvons laisser JUnit gérer le cycle de vie des containers.

Pour cela, JUnit propose les annotations `@Rule` et `@ClassRule`. Vous pouvez ainsi supprimer vos méthodes `@BeforeClass` et `AfterClass`pour utiliser une des annotations JUnit.

::: tip
testcontainers est étroitement couplé avec JUnit4.x. Dans le cas où vos tests fonctionnent avec JUnit 5, vous devrez importer la dépendance

```
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.11.2</version>
    <scope>test</scope>
</dependency>
```

pour pouvoir utiliser les extensions testcontainers.
:::

::: tip
Vous risquez de devoir faire appel à la méthode `withCreateContainerCmdModifier` qui permet de modifier les paramètres de création du container. ``
:::

## Singleton Container

En utilisant l'annotation `@ClassRule`, un container est démarré pour chacune des classes de tests. Ce n'est pas vraiment optimal et il est possible de faire en sorte que le container ne soit démarré qu'une fois pour l'ensemble de la suite de tests.

Utilisez une fonctionnalité du langage java vous permettra d'avoir un singleton et donc qu'une seule instance. 

Une fois le singleton mis en place, relancez la suite de tests et mesurez le temps d'exécution. Que constatez-vous ?

