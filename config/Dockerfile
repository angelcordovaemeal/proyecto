FROM eclipse-temurin:17-jdk

COPY config-repo /config-repo

COPY config/target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app.jar"]