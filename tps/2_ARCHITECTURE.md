# Architecture

L'application Spring PetClinic est une application spring boot classique avec une architecture SOA (Service Oriented Application). 

![Architecture SOA&](architecture.png)


Les classes se terminant par `*Controller` sont les endpoints HTTP exposant des services permettant de manipuler les entités.

On retrouve par exemple les classes `PetController`, `OwnerController`, `VetController`, etc. 

Ces derniers font appel à la couche DAO qui est responsable de communiquer avec la base de données.

La couche DAO est ici représentée par les interfaces `Repository`. Ce sont des interfaces car c'est le composant Spring Data qui fournira l'implémentation au runtime. 

Pour ce faire, les interfaces doivent étendre l'interface `org.springframework.data.repository.Repository` . Par contre, il est également possible d'ajouter ces propres méthodes d'accès à la base de données grâce à l'annotation `@Query`.

Un exemple de cette utilisation se trouve par exemple dans la classe `OwnerRepository` :

```java
@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
@Transactional(readOnly = true)
Collection<Owner> findByLastName(@Param("lastName") String lastName);
```

Ces méthodes de requêtages sont testées dans les classe de tests se terminant par `*RepositoryTests`. Par exemple `PetRepositoryTests`.

## Tests initiaux

Actuellement l'ensemble des tests `*RepositoryTests` étendent une classe commune nommée `AbstractRepositoryTest`.  
  
Cette classe permet un chargement allégé du context spring 
avec uniquement les interfaces des repositories et la base de donnée Inmemory par défaut fournit par springBootTest.

Pour bien démarrer, vous pouvez lancer la suite de test et mesurez le temps d'exécution.

Notez que les tests utilisent par défaut la configuration de l'application. 

Dans ce cas, il s'agit de `src/main/resouces/application.properties`. La configuration de la base est la suivante :

```
# database init, supports mysql too
database=hsqldb
spring.datasource.schema=classpath*:db/${database}/schema.sql
spring.datasource.data=classpath*:db/${database}/data.sql
```

## Amélioration des Tests

> L'objectif de cette partie est d'utiliser une base de donnée similaire à celle utilisée en production par l'application.  
> Pour cet atelier nous allons utiliser le SGBD MYSQL.

Afin de commencer la migration vers des tests utilisant une base de données MySQL, il vous faut tout d'abord ajouter la dépendance vers le driver mysql :  

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
``` 

Puis dans un second temps afin de prévoir l'interopérabilité avec Testcontainers il faut ajouter la librairie `org.testcontainers:testcontainers`  
ainsi que la libraire `org.testcontainers:mysql` qui permet d'avoir une pré-packagé d'un conteneur mysql.

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.11.2</version>
    <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testcontainers</groupId>
  <artifactId>mysql</artifactId>
  <version>1.11.2</version>
  <scope>test</scope>
</dependency>
```

Ensuite il vous faut modifier la configuration pour utiliser la nouvelle base de donnée. Ceci ce déroule en trois étapes :  

* Modification des properties de configuration de la datasource de l'application
* Modification de la classe commune `AbstractRepositoryTest` pour utiliser la Datasource nouvellement configuré
* Ajouter une première version de `GenericContainer` (objet java fourni par la librairie Testcontainers représentant un conteneur docker)

### Modification des properties

Pour ce faire, vous pouvez créer un fichier `application-test.properties` dans le dossier `src/test/resources` avec les informations suivantes :

```
spring.datasource.url=jdbc:mysql://localhost/petclinic
spring.datasource.username=petclinic
spring.datasource.password=petclinic
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Vous devez faire en sorte de charger ce fichier de configuration pour les classes de tests.

### Utilisation de la nouvelle dataSource

Pour utiliser la nouvelle datasource vous suffit d'ajouter l'annotation suivante sur la classe `AbstractRepositoryTest`: 

// TODO à cacher / ou à supprimer pour la branche de correction

```java
@TestPropertySource(locations="classpath:application-test.properties")
```

Par ailleurs, l'annotation `@DataJpaTest` de la dépendance spring boot test se charge de créer tout le nécessaire pour avoir un contexte de test unitaire opérationnel. 
C'est-à-dire qu'elle va notamment crée une base de données en mémoire (cf : ligne 12 ci-dessous).

```java{12}
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(DataJpaTestContextBootstrapper.class)
@ExtendWith(SpringExtension.class)
@OverrideAutoConfiguration(enabled = false)
@TypeExcludeFilters(DataJpaTypeExcludeFilter.class)
@Transactional
@AutoConfigureCache
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ImportAutoConfiguration
public @interface DataJpaTest {
    // ... TRUNCATED ...
}
```

Vous devez donc faire en sorte de surcharger ce comportement pour ne pas avoir de base de données en mémoire. Pour cela ajouter l'annotation suivante :  

// TODO à cacher / ou à supprimer pour la branche de correction

```java
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
```
  
> Cette annotation permet de dire à spring boot test de ne surcharger aucune data source lors des tests

--- 

## Vérification

Lancez les tests! S'ils plantent avec une belle exception

::: danger Connexion refusée
Caused by: java.net.ConnectException: Connexion refusée (Connection refused)
:::

c'est que la base de données en mémoire a bien été désactivé au profit de la base mysql. Plus aucun des tests de repository ne fonctionnent ! :) 


