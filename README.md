<img src="elibrary-controller/src/main/resources/static/img/logo/logo_200_200.png" alt="E-Library" align="right">

# E-Library

E-Library - the user-friendly general reference tool—delivers designed to support every range of user, including 
elementary students, college-prep and college-level researchers, and professional educators.

Using this web-application you can:
* look all available books in the library, search it by a category, author or title
* booking liked the book (then to pick up at the real library)
* track the status of your booking (reading)
* getting email if you remember return the book

The E-Library application does reading the simplest!

## Installing / Getting started

```shell
mvn spring-boot:run
```

When you execute the code above the application will run.

## Developing

### Modules
* elibrary-main
* elibrary-entity
* elibrary-service
* elibrary-api
* elibrary-controller
* elibrary-rest
* elibrary-web
* elibrary-utils
* elibrary-entity-metadata

### Built With
* Java 1.8
* Spring Boot 2.4.2
* Spring Data JPA
* Spring Web
* OAuth2 Client
* ModelMapper 2.3.9
* Hibernate Types 52 2.10.4
* H2 Database Engine 1.4.200
* Thymeleaf 3.0.12
* Mockito 3.6.28
* Lombok 1.18.16
* Liquibase	3.10.3

## Versioning

* 0.0.1-SNAPSHOT

## Tests

There is test some JpaRepositories and AuthorService

```shell
mvn test
```

## Style guide

[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Database

Has been used MySQL Community Server 8.0.25

Track, version, and deploy database changes with Liquibase

[Data base diagram](db-diagram.svg), [SQL scripts](db-scripts.sql)

## Licensing

No license