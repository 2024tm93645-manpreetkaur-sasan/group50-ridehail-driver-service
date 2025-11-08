# runtime image for Spring Boot fat JAR
FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/driver.jar
COPY ${JAR_FILE} /app/driver.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/driver.jar","--server.port=8081"]
