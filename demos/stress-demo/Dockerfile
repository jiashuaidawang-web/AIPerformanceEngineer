# ============================================================
# StressDemo — Dockerfile (Spring Boot 3.3.7 + JDK 21)
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime
FROM eclipse-temurin:21-jre-jammy

RUN groupadd -r stress && useradd -r -g stress stress \
    && mkdir -p /app && chown -R stress:stress /app

WORKDIR /app
COPY --from=builder --chown=stress:stress /app/target/stress-demo.jar app.jar

USER stress
EXPOSE 8090

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
    CMD wget --spider -q http://localhost:8090/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
