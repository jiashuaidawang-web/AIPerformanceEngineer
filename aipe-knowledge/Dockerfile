FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY aipe-knowledge/target/aipe-knowledge-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "-Djava.security.egd=file:/dev/./urandom"]
