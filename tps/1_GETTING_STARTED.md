# Getting Started

Dans cet atelier, vous allez mettre en place [Testcontainers]() pour migrer les tests d'intégrations d'une application spring boot.

Il s'agit de l'application [Spring Petclinic](https://github.com/spring-projects/spring-petclinic), une application de démonstration utilisée pour montrer des cas d'exemples de certains composants de l'écosystème Spring.
On y trouve par exemple [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators), [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/), [Spring Web](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html) et [Spring Cache](https://spring.io/guides/gs/caching/).


## Récupération du projet

Pour bien démarrer, assurez-vous d'avoir les outils suivant d'installés :

- [Git](https://git-scm.com/)
- [JDK 8 ou supérieur](https://www.java.com/fr/download/)
- [Maven](https://maven.apache.org/download.cgi)
- [docker](https://www.docker.com/get-started)
- Votre IDE préféré


Ensuite téléchargez le projet depuis github :

```bash
git clone https://github.com/vgallet/handson-testcontainers.git
```

## Lancement du projet

Ouvrez le projet dans votre IDE et assurez-vous que celui-ci build :

```bash
mvn clean package
```

::: tip
Le projet utilise du css compilé depuis des fichiers less. Si le css de l'application ne s'affiche pas correctement. Recompiler le css avec la commande :
```
mvn generate-resources
```
:::

Vous pouvez ensuite démarrer l'application en lançant le `main` de la classe `PetClinicApplication.java` où en démarrant directement le jar crée précédemment.

```bash
java -jar target/spring-petclinic-2.1.0.BUILD-SNAPSHOT.jar
``` 

Une fois l'application démarrée, rendez-vous ensuite sur la page [http://localhost:8080](http://localhost:8080).

À noter que l'application démarre en utilisant une base chargée en mémoire de type hsql.
La configuration de la base de données se trouve de l'application dans le fichier `application.properties`.

```
# database init, supports Mysql too
database=hsqldb
spring.datasource.schema=classpath*:db/${database}/schema.sql
spring.datasource.data=classpath*:db/${database}/data.sql
```

Ce type de base données est généralement utilisée pour les tests automatisés et c'est ce que nous allons changer grâce à Testcontainers.
