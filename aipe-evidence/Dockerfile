FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY aipe-evidence/target/aipe-evidence-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "-Djava.security.egd=file:/dev/./urandom"]
