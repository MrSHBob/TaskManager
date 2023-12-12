# Task manager

## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Gradle 7.6](https://gradle.org/releases/)

## Running the application locally

Run MySQL database container: 
go to "src\main\resources\docker_files\" directory and execute:

```shell
docker-compose -f DockerComposeTaskManagerDB.yml up
```

Run application:

```shell
./gradlew bootRun
```

Run tests:

```shell
./gradlew test
```
