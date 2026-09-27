# Multi-stage build: compile with the Maven wrapper on a JDK, then run on a slim JRE.
# Java version matches pom.xml's <java.version>.

# --- Build stage ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Resolve dependencies first, so they're cached until pom.xml changes
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Build the executable JAR (tests run in CI, not in the image build)
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests \
    && cp target/hpsc-web-*.jar app.jar \
    && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# --- Runtime stage ---
FROM eclipse-temurin:25-jre
WORKDIR /app

# curl for the health check, installed explicitly rather than relying on the base image to ship it
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/* \
    && groupadd --system hpsc && useradd --system --gid hpsc --no-create-home hpsc \
    && mkdir logs && chown hpsc:hpsc logs

# Copy Spring Boot's layers from least to most frequently changing, for better image-layer caching
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

USER hpsc

# The prod profile's datasource URL points at localhost, so supply SPRING_DATASOURCE_URL for the database's host,
# plus MYSQL_USER and MYSQL_PASSWORD, at run time
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:MaxRAMPercentage=75"

EXPOSE 8080

# Actuator's health endpoint (under the /hpsc-web context path) reports DOWN when the database is unreachable too.
# The start period allows for Flyway migrating an empty database on first start.
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD curl -fsS http://localhost:8080/hpsc-web/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
