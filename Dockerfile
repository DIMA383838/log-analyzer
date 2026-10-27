ARG RUNTIME_IMAGE=eclipse-temurin:24-jre

FROM ${RUNTIME_IMAGE} AS jdk

WORKDIR /app
USER root
COPY target/hw3-logs-1.0.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
