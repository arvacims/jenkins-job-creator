FROM maven:3.5-jdk-8-alpine AS builder

COPY src/       /workspace/src
COPY pom.xml    /workspace/pom.xml

WORKDIR /workspace

RUN mvn clean verify


FROM openjdk:8-jre-alpine

COPY --from=builder /workspace/target/app.jar /app/
COPY data/ /app/data

EXPOSE 8080

WORKDIR /app/
ENV JAVA_OPTS ""
CMD exec java ${JAVA_OPTS} -jar app.jar
