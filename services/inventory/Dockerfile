# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 1. Copy the common library to the parent directory of /app
COPY services/common /common

# 2. Copy inventory service files
COPY services/inventory/build.gradle.kts services/inventory/settings.gradle.kts services/inventory/gradlew ./
COPY services/inventory/gradle ./gradle
COPY services/inventory/src ./src

# Build the application (skip tests)
RUN ./gradlew build -x test --no-daemon

# Find and copy JAR
RUN find /app/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8082
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Execute
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
