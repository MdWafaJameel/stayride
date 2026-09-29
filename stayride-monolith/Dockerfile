FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/stayride-*.jar app.jar

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "app.jar"]