FROM openjdk:17

ARG ENVIRONMENT
ARG REDIS_HOST


ENV ENVIRONMENT=$ENVIRONMENT
ENV REDIS_HOST=$REDIS_HOST


COPY build/*.jar app.jar
ENTRYPOINT java -jar /app.jar || (echo "App failed. Waiting for 100 seconds before exiting..." && sleep 100)
