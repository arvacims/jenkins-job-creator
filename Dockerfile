FROM maven:3.5.2-jdk-8-alpine AS builder

COPY src/       /workspace/src
COPY pom.xml    /workspace/pom.xml

WORKDIR /workspace

RUN mvn clean verify


FROM openjdk:8u151-jre-alpine

COPY --from=builder /workspace/target/app.jar /app/

EXPOSE 8080

WORKDIR /app/
ENV JAVA_OPTS ""
CMD exec java ${JAVA_OPTS} -jar app.jar
