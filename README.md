# Covid19 Germany API

This application has the primary responsibility of performing covid19 statistics search for Germany.

## Useful links

1) Base api: https://api.corona-zahlen.org/docs/endpoints/germany.html#germany-2

## Pre-requirements

- Java 17
- GIT
- Docker
- docker-compose

## Application run

###Local:

1) Check that you have installed jdk at least 17 version.
2) Run docker-compose file at `./docker-compose.yml` to start Redis
3) Run "Spring" application:
    - Console:
      `spring_profiles_active=local ./gradlew run`
    - Idea:
      Use Idea runner configuration that was preinstalled automatically.

4) Application is running on port: `8090`.
5) Check Swagger API is available: http://localhost:8090/swagger-ui/index.html#/
6) Run sample request: curl --location 'localhost:8090/api/v1/statistics?fromDate=20.05.2023&toDate=25.05.2023' or
   curl --location 'localhost:8090/api/v1/statistics/months/2'

## Branching and deployment

Process based on Github Actions is configured. Currently, no any steps except build and test are present.

Currently, there are no any working environment except 'local'.