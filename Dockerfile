FROM openjdk:8u151-jre-alpine

COPY target/app.jar /app/

EXPOSE 8080

WORKDIR /app/
ENV JAVA_OPTS ""
CMD exec java ${JAVA_OPTS} -jar app.jar
