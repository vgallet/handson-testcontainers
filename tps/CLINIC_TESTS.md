# Architecture

L'application Spring PetClinic est une application spring boot classique avec une architecture SOA. 

![Architecture SOA&](architecture.png)


Les classes se terminant par `*Controller` sont les endpoints HTTP exposant des services permettant de manipuler les entités.

On retrouve par exemple les classes `PetController`, `OwnerController`, `VetController`, etc. 
Ces derniers font appel à la couche DAO qui est en charge de communiquer avec la base de données.

La couche DAO est ici représentée par les interfaces `Repository`. Ce sont des interfaces car c'est le composant Spring Data qui fournira l'implémentation au runtime. 
Pour ce faire, les interfaces doivent étendre l'interface `org.springframework.data.repository.Repository` . Par contre, il est également possible d'ajouter ces propres méthodes d'accès à la base de données grâce à l'annotation `@Query`.
Un exemple de cette utilisation se trouve par exemple dans la classe `OwnerRepository` :

```java
@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
@Transactional(readOnly = true)
Collection<Owner> findByLastName(@Param("lastName") String lastName);
```

Ces méthodes de requêtages sont testées dans les classe de tests se terminant par `*RepositoryTests`. Par exemple `PetRepositoryTests`.

## Les Tests

Pour bien démarrer, vous pouvez lancer la suite de test et mesurez le temps d'exécution.

Notez que les tests utilisent par défaut la configuration de l'application. 

Dans ce cas, il s'agit de `src/main/resouces/application.properties`. Il s'agit de la configuration de la base suivante :

```
# database init, supports mysql too
database=hsqldb
spring.datasource.schema=classpath*:db/${database}/schema.sql
spring.datasource.data=classpath*:db/${database}/data.sql
```

Afin de commencer la migration vers des tests utilisant une base de données MySQL, il vous faut ajouter la dépendance vers `testcontainers`.

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.11.2</version>
    <scope>test</scope>
</dependency>
```

Ensuite, il vous faut modifier la configuration à la base pour les tests. Cette dernière se trouve dans la classe `AbstractIntegrationTests` et est commune à tous les tests nécessitant un accès à la base de données.

Pour ce faire, vous pouvez créer un fichier `application-test.properties` dans le dossier `src/test/resources` avec les informations suivantes :

```
spring.datasource.url=jdbc:mysql://localhost/petclinic
spring.datasource.username=petclinic
spring.datasource.password=petclinic
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

Pour charger cette nouvelle configuration pour notre classe de tests, vous pouvez ensuite utiliser l'annotation `@TestPropertySource`. 

Par ailleurs, Spring va par défaut créer une base de données en mémoire pour les tests. Vous pouvez surcharger ce comportement en utilisant l'annotation `@AutoConfigureTestDatabase`


```java
@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@TestPropertySource(locations="classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AbstractIntegrationTests {
...
}
```




