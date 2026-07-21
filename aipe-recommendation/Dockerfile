FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY aipe-recommendation/target/aipe-recommendation-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "-Djava.security.egd=file:/dev/./urandom"]
