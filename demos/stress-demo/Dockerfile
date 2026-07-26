FROM openjdk:8-jdk-alpine
RUN addgroup -S stress && adduser -S stress -G stress
WORKDIR /app
COPY target/stress-demo.jar app.jar
USER stress
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
