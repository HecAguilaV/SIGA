# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 1. Copy the common library
COPY services/common /common

# 2. Copy billing service files
COPY services/billing/build.gradle.kts services/billing/settings.gradle.kts services/billing/gradlew ./
COPY services/billing/gradle ./gradle
COPY services/billing/src ./src

# Build the application (skip tests)
RUN ./gradlew build -x test --no-daemon

# Find and copy JAR
RUN find /app/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8084
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Execute
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]