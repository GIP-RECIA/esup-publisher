esup-publisher-ui
=================

Application to publish content into several/different apps (uPortal portlet apps as example but not only!)

Licensing
---------

The project source code is licensed under Apache-2.0. Third-party backend notices are listed in [NOTICE](NOTICE).
Third-party frontend notices, including license texts, are delivered with the application at
`/publisher/ui/NOTICE-frontend.txt`; see [doc/licensing.md](doc/licensing.md) for generation and distribution details.

[![Coverage](https://raw.githubusercontent.com/GIP-RECIA/esup-publisher/badges/jacoco.svg)](https://github.com/GIP-RECIA/esup-publisher/actions/workflows/maven.yml)
[![Branches](https://raw.githubusercontent.com/GIP-RECIA/esup-publisher/badges/branches.svg)](https://github.com/GIP-RECIA/esup-publisher/actions/workflows/maven.yml)

Database Initialization
-----------------------

### Requirement

The database server should be a recent version supporting utf8mb4 characters and the collation indicated bellow.
As example you can use this [mariadb configuration](https://github.com/GIP-RECIA/docker-mariadb/) the application is qualified on a such server.

Running with openjdk 8 or 11

### Initialization

```sql
create database publisher DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci;
```

run mvn command to init the database

```shell
./mvnw compile liquibase:update
```

or deploy and run directly the app

for more command details see doc/dev.txt info
