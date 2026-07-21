# ============================================================
# StressDemo — Dockerfile (Spring Boot 2.7.18 + JDK 1.8)
# ============================================================
FROM maven:3.9-eclipse-temurin-8 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime
FROM openjdk:8-jdk-alpine

RUN addgroup -S stress && adduser -S stress -G stress \
    && mkdir -p /app && chown -R stress:stress /app

WORKDIR /app
COPY --from=builder --chown=stress:stress /app/target/stress-demo.jar app.jar

USER stress
EXPOSE 8090

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD wget --spider -q http://localhost:8090/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
