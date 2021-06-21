<img src="elibrary-controller/src/main/resources/static/img/logo/logo_200_200.png" alt="E-Library" align="right">

# E-Library

E-Library - the user-friendly general reference tool—delivers designed to support every range of user, including 
elementary students, college-prep and college-level researchers, and professional educators.

Using this web-application you can/be able to:
* Check all books available in the library now, look for necessary book using category, author or title and immediately book it
* Then you can to get selected book pick it up at the real library
* Track the status of your booking (reading)
* In case you have forgotten to return the book to the library you will get email, just as kind reminder.

The E-Library application does reading simple!

## Installing / Getting started

### With docker

1. Build image
```shell
docker build -t elibrary-spring-framework .
```

2. Run application with docker-compose.yml
```shell
docker-compose up
```

When you execute the code above the application will run.

## Developing

### Modules
* elibrary-main
* elibrary-entity
* elibrary-service
* elibrary-api
* elibrary-logger 
* elibrary-rest
* elibrary-web
* elibrary-utils
* elibrary-entity-metadata

### Built With
* Java 1.8
* Spring framework 5.2.15.RELEASE
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

There is test some JpaRepositories and RestControllers

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